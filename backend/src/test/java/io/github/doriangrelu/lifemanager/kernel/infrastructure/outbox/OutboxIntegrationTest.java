package io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox;

import io.github.doriangrelu.lifemanager.bills.contract.event.ChargePaidV1;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.repository.BillsOutboxJdbcRepository;
import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdCreatedV1;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.json.JsonMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.transaction.TransactionOperations;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.sql.Connection;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Boîte d'envoi : rien de perdu après un arrêt, ordre gardé malgré les erreurs, rien pour un changement annulé. */
// Sans transaction de test : les écritures doivent être réellement validées, comme en production.
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OutboxIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    @Named("bills")
    Outbox outbox;

    @Inject
    @Named("bills")
    TransactionOperations<Connection> transactions;

    @Inject
    BillsOutboxJdbcRepository table;

    @Inject
    JsonMapper json;

    @Inject
    ProbeListener probe;

    @Inject
    ApplicationEventPublisher<Object> eventBus;

    BlockingHttpClient http;
    HouseholdApi households;

    @BeforeAll
    void setUp() {
        http = client.toBlocking();
        households = new HouseholdApi(http);
    }

    @Test
    void an_event_left_behind_by_a_crash_is_delivered_on_the_next_round() throws IOException {
        var owner = newUser();
        var ownerId = whoami(owner);
        var household = households.create(owner, "Maison").id();
        // État laissé par un arrêt brutal : l'événement est enregistré mais n'a jamais été distribué.
        var paid = new ChargePaidV1(UUID.randomUUID(), household, "internet", 3999, TODAY, UUID.randomUUID(),
                ownerId, null, Instant.now());
        var payload = json.writeValueAsString(paid);
        transactions.executeWrite(status -> table.save(OutboxEntity.pending(ChargePaidV1.class.getName(),
                payload, Instant.now())));

        outbox.deliver();

        assertThat(expenses(owner, household)).containsExactly(Map.entry("internet", 3999));
    }

    @Test
    void a_failing_recipient_gets_the_event_again_and_the_order_is_kept() {
        probe.failFor(1, 1);

        // 1 est refusé à la validation : 2 attend son tour au lieu de passer devant.
        recordInOneTransaction(1, 2);
        outbox.deliver();

        assertThat(probe.received()).filteredOn(n -> n == 1 || n == 2).containsExactly(1, 2);
    }

    @Test
    void an_event_refused_too_often_is_set_aside_without_blocking_the_next_ones() {
        probe.failFor(10, Integer.MAX_VALUE);

        recordInOneTransaction(10, 11);
        for (int round = 0; round < Outbox.MAX_ATTEMPTS; round++) {
            outbox.deliver();
        }

        assertThat(probe.received()).contains(11).doesNotContain(10);
    }

    @Test
    void a_cancelled_change_sends_nothing() {
        assertThatThrownBy(() -> transactions.executeWrite(status -> {
            outbox.record(new ProbeEventV1(20));
            throw new IllegalStateException("changement annulé");
        })).isInstanceOf(IllegalStateException.class);

        outbox.deliver();

        assertThat(probe.received()).doesNotContain(20);
    }

    @Test
    void a_household_created_twice_keeps_one_set_of_defaults() {
        var owner = newUser();
        var ownerId = whoami(owner);
        var household = households.create(owner, "Maison").id();
        var categories = count(owner, "/api/households/" + household + "/expense-categories");
        var lists = count(owner, "/api/households/" + household + "/shopping-lists");

        eventBus.publishEvent(new HouseholdCreatedV1(household, ownerId, Instant.now()));

        assertThat(count(owner, "/api/households/" + household + "/expense-categories")).isEqualTo(categories);
        assertThat(count(owner, "/api/households/" + household + "/shopping-lists")).isEqualTo(lists);
    }

    private void recordInOneTransaction(int... numbers) {
        transactions.executeWrite(status -> {
            for (int number : numbers) {
                outbox.record(new ProbeEventV1(number));
            }
            return null;
        });
    }

    private UUID whoami(String token) {
        return UUID.fromString(String.valueOf(http.retrieve(HttpRequest.GET("/api/whoami").bearerAuth(token),
                Map.class).get("userId")));
    }

    private int count(String token, String path) {
        return http.retrieve(HttpRequest.GET(path).bearerAuth(token), Argument.listOf(Map.class)).size();
    }

    @SuppressWarnings("unchecked")
    private List<Map.Entry<String, Integer>> expenses(String token, UUID household) {
        var month = TODAY.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Map<String, Object> budget = http.retrieve(HttpRequest.GET("/api/households/" + household + "/budget/"
                + month).bearerAuth(token), Argument.mapOf(String.class, Object.class));
        return ((List<Map<String, Object>>) budget.get("expenses")).stream()
                .map(expense -> Map.entry(String.valueOf(expense.get("label")), (Integer) expense.get("amountCents")))
                .toList();
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
