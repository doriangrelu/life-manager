package io.github.doriangrelu.lifemanager.stabilisation;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lien « échéance → dépense / rentrée » dont la dépense ou la rentrée a été supprimée par un membre :
 * il reste (pour qu'elle ne revienne pas), mais doit disparaître avec le foyer.
 */
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChargeLinksErasureIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));

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
    void links_of_deleted_expenses_and_incomes_go_with_the_household() throws SQLException {
        var owner = newUser();
        var ownerId = UUID.fromString(String.valueOf(http.retrieve(HttpRequest.GET("/api/whoami").bearerAuth(owner),
                Map.class).get("userId")));
        var household = households.create(owner, "Maison").id();
        households.trackIncome(owner, household, true);
        addCharge(owner, household, ownerId, firstCategory(owner, household, "expense-categories"), null);
        addCharge(owner, household, ownerId, firstCategory(owner, household, "income-categories"), "INCOME");
        var month = month(owner, household);
        var expense = firstId(month.get("expenses"));
        var income = firstId(month.get("incomes"));
        // Un membre supprime la dépense et la rentrée produites par les échéances : les liens restent.
        http.exchange(HttpRequest.DELETE(route(household, "/expenses/" + expense)).bearerAuth(owner));
        http.exchange(HttpRequest.DELETE(route(household, "/incomes/" + income)).bearerAuth(owner));
        assertThat(count("SELECT count(*) FROM budget.charge_expense WHERE expense_id = ?", expense)).isOne();

        households.delete(owner, household, "maison");

        assertThat(count("SELECT count(*) FROM budget.charge_expense WHERE expense_id = ?", expense)).isZero();
        assertThat(count("SELECT count(*) FROM budget.charge_income WHERE income_id = ?", income)).isZero();
    }

    private UUID firstCategory(String token, UUID household, String kind) {
        List<Map<String, Object>> categories = http.retrieve(HttpRequest.GET(route(household, "/" + kind))
                .bearerAuth(token), Argument.listOf(Argument.mapOf(String.class, Object.class)));
        return UUID.fromString(String.valueOf(categories.getFirst().get("id")));
    }

    private void addCharge(String token, UUID household, UUID payer, UUID category, String direction) {
        var body = new HashMap<String, Object>();
        body.put("name", direction == null ? "Internet" : "Salaire");
        body.put("amountCents", 3999);
        body.put("amountKind", "FIXED");
        body.put("frequency", "MONTHLY");
        body.put("firstDueOn", TODAY.toString());
        body.put("budgetCategoryId", category);
        body.put("payerId", payer);
        body.put("direction", direction);
        http.exchange(HttpRequest.POST(route(household, "/charges"), body).bearerAuth(token));
    }

    private Map<String, Object> month(String token, UUID household) {
        return http.retrieve(HttpRequest.GET(route(household, "/budget/" + TODAY.toString().substring(0, 7)))
                .bearerAuth(token), Argument.mapOf(String.class, Object.class));
    }

    @SuppressWarnings("unchecked")
    private static UUID firstId(Object rows) {
        return UUID.fromString(String.valueOf(((List<Map<String, Object>>) rows).getFirst().get("id")));
    }

    private static String route(UUID household, String path) {
        return "/api/households/" + household + path;
    }

    private static long count(String sql, UUID id) throws SQLException {
        try (var connection = DriverManager.getConnection(postgres().getJdbcUrl(), postgres().getUsername(),
                postgres().getPassword()); var statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (var result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
