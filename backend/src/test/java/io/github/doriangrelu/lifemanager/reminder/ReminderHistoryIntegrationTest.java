package io.github.doriangrelu.lifemanager.reminder;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.PurgeReminderHistory;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.PersonalReminderRequest;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.PersonalReminderResponse;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.ReminderDoneResponse;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity.ReminderDoneEntity;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.repository.ReminderDoneJdbcRepository;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Historique des rappels faits : trace de « c'est fait », pages, retrait d'une ligne, purge, cloisonnement. */
// Sans transaction de test : les lignes écrites directement doivent être vues par l'API.
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReminderHistoryIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    ReminderDoneJdbcRepository table;

    @Inject
    PurgeReminderHistory purge;

    BlockingHttpClient http;
    String owner;
    UUID household;

    @BeforeEach
    void newHousehold() {
        http = client.toBlocking();
        owner = newUser();
        household = new HouseholdApi(http).create(owner, "Maison").id();
    }

    @Test
    void done_writes_a_line_that_outlives_the_reminder() {
        var monthly = add(new PersonalReminderRequest("Relevé", null, TODAY.minusDays(3), "MONTHLY", "SAME_DAY"));
        var once = add(new PersonalReminderRequest("Mutuelle", null, TODAY.plusDays(2), "NONE", "SAME_DAY"));

        done(monthly.id());
        done(once.id());
        http.exchange(HttpRequest.DELETE(reminders() + "/" + monthly.id()).bearerAuth(owner));

        var lines = history(0);
        assertThat(lines).extracting(ReminderDoneResponse::title, ReminderDoneResponse::dueOn)
                .containsExactly(org.assertj.core.groups.Tuple.tuple("Mutuelle", TODAY.plusDays(2)),
                        org.assertj.core.groups.Tuple.tuple("Relevé", TODAY.minusDays(3)));
        assertThat(lines).allSatisfy(line -> assertThat(line.doneBy()).isNotNull());
    }

    @Test
    void history_comes_in_pages_of_twenty() {
        var monthly = add(new PersonalReminderRequest("Relevé", null, TODAY, "MONTHLY", "SAME_DAY"));
        for (int i = 0; i < 21; i++) {
            done(monthly.id());
        }

        assertThat(history(0)).hasSize(20);
        assertThat(history(20)).hasSize(1);
        assertThat(failure(() -> history(-1)).code()).isEqualTo(400);
    }

    @Test
    void a_line_can_be_removed_by_a_member_only() {
        var stranger = newUser();
        done(add(new PersonalReminderRequest("Ramonage", null, TODAY, "YEARLY", "SAME_DAY")).id());
        var line = history(0).getFirst();

        assertThat(failure(() -> http.retrieve(HttpRequest.GET(base()).bearerAuth(stranger))).code()).isEqualTo(404);
        assertThat(failure(() -> http.exchange(HttpRequest.DELETE(base() + "/" + line.id()).bearerAuth(stranger)))
                .code()).isEqualTo(404);
        assertThat(failure(() -> http.exchange(HttpRequest.DELETE(base() + "/" + UUID.randomUUID())
                .bearerAuth(owner))).code()).isEqualTo(404);

        http.exchange(HttpRequest.DELETE(base() + "/" + line.id()).bearerAuth(owner));
        assertThat(history(0)).isEmpty();
    }

    @Test
    void lines_older_than_one_year_are_purged() {
        var old = new ReminderDoneEntity(UUID.randomUUID(), household, "Vieux", TODAY.minusYears(2),
                Instant.now().minus(400, ChronoUnit.DAYS), UUID.randomUUID());
        var recent = new ReminderDoneEntity(UUID.randomUUID(), household, "Récent", TODAY.minusMonths(11),
                Instant.now().minus(300, ChronoUnit.DAYS), UUID.randomUUID());
        table.save(old);
        table.save(recent);

        purge.purge();

        assertThat(history(0)).extracting(ReminderDoneResponse::title).containsExactly("Récent");
    }

    private PersonalReminderResponse add(PersonalReminderRequest request) {
        return http.retrieve(HttpRequest.POST(reminders(), request).bearerAuth(owner), PersonalReminderResponse.class);
    }

    private void done(UUID reminder) {
        http.exchange(HttpRequest.POST(reminders() + "/" + reminder + "/done", "").bearerAuth(owner));
    }

    private List<ReminderDoneResponse> history(int offset) {
        return http.retrieve(HttpRequest.GET(base() + "?offset=" + offset).bearerAuth(owner),
                Argument.listOf(ReminderDoneResponse.class));
    }

    private String reminders() {
        return "/api/households/" + household + "/personal-reminders";
    }

    private String base() {
        return "/api/households/" + household + "/reminder-history";
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
