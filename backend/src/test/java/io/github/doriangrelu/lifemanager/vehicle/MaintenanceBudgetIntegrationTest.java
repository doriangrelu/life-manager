package io.github.doriangrelu.lifemanager.vehicle;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Lot Véhicules (v0.15.0) : le coût d'une opération d'entretien devient une dépense du budget. */
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaintenanceBudgetIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));
    private static final Argument<Map<String, Object>> JSON = Argument.mapOf(String.class, Object.class);
    private static final Argument<List<Map<String, Object>>> JSON_LIST = Argument.listOf(JSON);

    @Inject
    @Client("/")
    HttpClient client;

    BlockingHttpClient http;
    HouseholdApi households;

    @BeforeAll
    void setUp() {
        http = client.toBlocking();
        households = new HouseholdApi(http);
    }

    @Test
    void a_cost_sent_to_the_budget_becomes_an_expense_and_leaves_with_its_operation() {
        var home = newHome();
        var clio = addVehicle(home);
        var transport = category(home);

        var service = record(home, clio, 32_000L, Map.of("categoryId", transport, "payerId", home.ownerId));
        record(home, clio, 8_000L, null);

        assertThat(service).containsEntry("inBudget", true);
        assertThat(total(home)).isEqualTo(32_000);
        assertThat(labels(home)).contains("révision · clio");

        // Ancienne opération ajoutée après coup, une seule fois.
        var old = logbook(home, clio).stream().filter(e -> Boolean.FALSE.equals(e.get("inBudget"))).findFirst()
                .orElseThrow();
        sendToBudget(home, clio, id(old), transport);
        assertThat(total(home)).isEqualTo(40_000);
        assertThat(failure(() -> sendToBudget(home, clio, id(old), transport)).getStatus().getCode())
                .isEqualTo(HttpStatus.CONFLICT.getCode());
        assertThat(logbook(home, clio)).allSatisfy(e -> assertThat(e).containsEntry("inBudget", true));

        // Opération supprimée : sa dépense part aussi.
        http.exchange(HttpRequest.DELETE(maintenance(home, clio) + "/" + id(service)).bearerAuth(home.owner));
        assertThat(total(home)).isEqualTo(8_000);

        // Véhicule supprimé : la dépense restante reste dans le budget.
        http.exchange(HttpRequest.DELETE(route(home, "/vehicles/" + clio)).bearerAuth(home.owner));
        assertThat(total(home)).isEqualTo(8_000);
    }

    @Test
    void invalid_budget_choices_are_refused_and_nothing_is_recorded() {
        var home = newHome();
        var stranger = newHome();
        var clio = addVehicle(home);
        var transport = category(home);

        assertThat(failure(() -> record(home, clio, 5_000L, Map.of("categoryId", transport,
                "payerId", stranger.ownerId))).getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(failure(() -> record(home, clio, null, Map.of("categoryId", transport,
                "payerId", home.ownerId))).getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(logbook(home, clio)).isEmpty();
        assertThat(total(home)).isEqualTo(0);

        var free = record(home, clio, 0L, null);
        assertThat(failure(() -> sendToBudget(home, clio, id(free), transport)).getStatus().getCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(failure(() -> sendToBudget(stranger, clio, id(free), transport)).getStatus().getCode())
                .isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    // ---------- outils ----------

    private record Home(String owner, UUID ownerId, UUID household) {
    }

    private Home newHome() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        var ownerId = UUID.fromString(String.valueOf(http.retrieve(HttpRequest.GET("/api/whoami").bearerAuth(token),
                Map.class).get("userId")));
        return new Home(token, ownerId, households.create(token, "Maison").id());
    }

    private UUID addVehicle(Home home) {
        return id(http.retrieve(HttpRequest.POST(route(home, "/vehicles"), Map.of("name", "Clio"))
                .bearerAuth(home.owner), JSON));
    }

    private Object category(Home home) {
        List<Map<String, Object>> categories = http.retrieve(HttpRequest.GET(route(home, "/expense-categories"))
                .bearerAuth(home.owner), JSON_LIST);
        return categories.getFirst().get("id");
    }

    private Map<String, Object> record(Home home, UUID vehicle, Long cents, Map<String, Object> budget) {
        var body = new HashMap<String, Object>();
        body.put("performedOn", TODAY.toString());
        body.put("kind", "SERVICE");
        body.put("costCents", cents);
        body.put("budget", budget);
        return http.retrieve(HttpRequest.POST(maintenance(home, vehicle), body).bearerAuth(home.owner), JSON);
    }

    private void sendToBudget(Home home, UUID vehicle, UUID entry, Object category) {
        http.exchange(HttpRequest.POST(maintenance(home, vehicle) + "/" + entry + "/budget",
                Map.of("categoryId", category, "payerId", home.ownerId)).bearerAuth(home.owner));
    }

    private List<Map<String, Object>> logbook(Home home, UUID vehicle) {
        return http.retrieve(HttpRequest.GET(maintenance(home, vehicle)).bearerAuth(home.owner), JSON_LIST);
    }

    private Map<String, Object> month(Home home) {
        return http.retrieve(HttpRequest.GET(route(home, "/budget/" + TODAY.toString().substring(0, 7)))
                .bearerAuth(home.owner), JSON);
    }

    private Number total(Home home) {
        return (Number) month(home).get("totalCents");
    }

    @SuppressWarnings("unchecked")
    private List<Object> labels(Home home) {
        return ((List<Map<String, Object>>) month(home).get("expenses")).stream()
                .map(e -> e.get("label"))
                .toList();
    }

    private static UUID id(Map<String, Object> row) {
        return UUID.fromString(String.valueOf(row.get("id")));
    }

    private static String maintenance(Home home, UUID vehicle) {
        return route(home, "/vehicles/" + vehicle + "/maintenance");
    }

    private static String route(Home home, String path) {
        return "/api/households/" + home.household + path;
    }
}
