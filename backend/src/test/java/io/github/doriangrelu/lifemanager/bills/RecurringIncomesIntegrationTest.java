package io.github.doriangrelu.lifemanager.bills;

import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ChargeRequest;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ChargeResponse;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ForecastResponse;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Rentrées récurrentes (salaire…) : rentrée du budget à l'échéance, et prévisions de la période. */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecurringIncomesIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));
    private static final String MONTH = TODAY.format(DateTimeFormatter.ofPattern("yyyy-MM"));

    @Inject
    @Client("/")
    HttpClient client;

    BlockingHttpClient http;
    HouseholdApi households;
    String owner;
    UUID ownerId;
    UUID household;

    @BeforeAll
    void setUp() {
        http = client.toBlocking();
        households = new HouseholdApi(http);
    }

    @Test
    void a_recurring_income_due_today_becomes_an_income_not_an_expense() {
        newHome();

        var salary = add("Salaire", 245_000, TODAY, "INCOME", category("income-categories", "salaire"));

        assertThat(salary.direction()).isEqualTo("INCOME");
        var month = month();
        assertThat((List<?>) month.get("expenses")).isEmpty();
        assertThat(month.get("incomeCents")).isEqualTo(245_000);
        assertThat(String.valueOf(((List<Map<String, Object>>) month.get("incomes")).getFirst().get("receivedBy")))
                .isEqualTo(ownerId.toString());
    }

    @Test
    void the_forecast_lists_open_and_future_occurrences_with_their_direction() {
        newHome();
        add("Internet", 3999, TODAY.plusDays(3), null, category("expense-categories", "abonnements"));
        add("Salaire", 245_000, TODAY, "INCOME", category("income-categories", "salaire"));

        var forecast = forecast(TODAY.toString(), TODAY.plusMonths(2).toString());

        assertThat(forecast).extracting(item -> item.name() + " " + item.status() + " " + item.direction())
                .containsExactly("internet UPCOMING EXPENSE", "salaire UPCOMING INCOME",
                        "internet PLANNED EXPENSE", "salaire PLANNED INCOME");
        assertThat(forecast.get(2).occurrenceId()).isNull();
        assertThat(forecast.get(3).expectedCents()).isEqualTo(245_000);
    }

    @Test
    void the_forecast_refuses_an_invalid_period_and_strangers() {
        newHome();
        var stranger = newUser();

        assertThat(failure(() -> forecast(TODAY.toString(), TODAY.minusDays(1).toString())).code()).isEqualTo(400);
        assertThat(failure(() -> forecast(TODAY.toString(), TODAY.plusYears(3).toString())).code()).isEqualTo(400);
        assertThat(failure(() -> http.retrieve(HttpRequest.GET(charges() + "/forecast?from=" + TODAY + "&to=" + TODAY)
                .bearerAuth(stranger))).code()).isEqualTo(404);
    }

    private void newHome() {
        owner = newUser();
        ownerId = UUID.fromString(String.valueOf(http.retrieve(HttpRequest.GET("/api/whoami").bearerAuth(owner),
                Map.class).get("userId")));
        household = households.create(owner, "Maison").id();
    }

    private UUID category(String kind, String name) {
        List<Map<String, Object>> categories = http.retrieve(HttpRequest.GET("/api/households/" + household + "/"
                + kind).bearerAuth(owner), Argument.listOf(Argument.mapOf(String.class, Object.class)));
        return categories.stream().filter(category -> name.equals(category.get("name")))
                .map(category -> UUID.fromString(String.valueOf(category.get("id")))).findFirst().orElseThrow();
    }

    private ChargeResponse add(String name, long cents, LocalDate firstDueOn, String direction, UUID category) {
        var request = new ChargeRequest(name, cents, "FIXED", "MONTHLY", firstDueOn, category, ownerId, null,
                direction, null, null);
        return http.retrieve(HttpRequest.POST(charges(), request).bearerAuth(owner), ChargeResponse.class);
    }

    private List<ForecastResponse> forecast(String from, String to) {
        return http.retrieve(HttpRequest.GET(charges() + "/forecast?from=" + from + "&to=" + to).bearerAuth(owner),
                Argument.listOf(ForecastResponse.class));
    }

    private Map<String, Object> month() {
        return http.retrieve(HttpRequest.GET("/api/households/" + household + "/budget/" + MONTH).bearerAuth(owner),
                Argument.mapOf(String.class, Object.class));
    }

    private String charges() {
        return "/api/households/" + household + "/charges";
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
