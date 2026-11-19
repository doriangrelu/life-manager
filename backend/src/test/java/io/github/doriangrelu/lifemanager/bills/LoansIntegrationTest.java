package io.github.doriangrelu.lifemanager.bills;

import io.github.doriangrelu.lifemanager.bills.application.port.in.AdvanceChargeSchedules;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ChargeRequest;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ChargeResponse;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ForecastResponse;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.LoanRequest;
import io.github.doriangrelu.lifemanager.household.HouseholdApi;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Crédits : une mensualité (prêt + assurance) payée seule chaque mois, jusqu'à la dernière échéance. */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoansIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));
    private static final String MONTH = TODAY.format(DateTimeFormatter.ofPattern("yyyy-MM"));

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    AdvanceChargeSchedules advanceSchedules;

    BlockingHttpClient http;
    String owner;
    UUID ownerId;
    UUID household;
    UUID category;

    @BeforeEach
    void newHome() {
        http = client.toBlocking();
        owner = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(owner, UUID.randomUUID() + "@example.org", "Membre");
        ownerId = UUID.fromString(String.valueOf(http.retrieve(HttpRequest.GET("/api/whoami").bearerAuth(owner),
                Map.class).get("userId")));
        household = new HouseholdApi(http).create(owner, "Maison").id();
        List<Map<String, Object>> categories = http.retrieve(HttpRequest.GET("/api/households/" + household
                + "/expense-categories").bearerAuth(owner), Argument.listOf(Argument.mapOf(String.class, Object.class)));
        category = UUID.fromString(String.valueOf(categories.getFirst().get("id")));
    }

    @Test
    void a_loan_is_paid_as_a_single_expense_and_stops_at_its_last_instalment() {
        var loan = add(loan(61_240, 2_400L, TODAY, TODAY.plusMonths(1)));

        assertThat(loan.loan().insuranceCents()).isEqualTo(2_400);
        assertThat(loan.lastDueOn()).isEqualTo(TODAY.plusMonths(1));
        assertThat(loan.active()).isTrue();
        assertThat(loan.next().dueOn()).isEqualTo(TODAY.plusMonths(1));
        assertThat(expenseCents()).containsExactly(61_240);
        // Les prévisions s'arrêtent à la dernière mensualité.
        assertThat(forecastDates(TODAY.plusDays(1), TODAY.plusMonths(6))).containsExactly(TODAY.plusMonths(1));
    }

    @Test
    void a_loan_whose_last_instalment_is_today_is_finished() {
        var loan = add(loan(30_000, 0L, TODAY, TODAY));

        assertThat(loan.active()).isFalse();
        assertThat(loan.next()).isNull();
        assertThat(expenseCents()).containsExactly(30_000);
        assertThat(forecastDates(TODAY, TODAY.plusMonths(6))).isEmpty();
        advanceSchedules.advanceAll();
        assertThat(expenseCents()).hasSize(1);
    }

    @Test
    void an_ordinary_charge_is_not_a_loan_and_has_no_end() {
        var internet = add(new ChargeRequest("Internet", 3_999, "FIXED", "MONTHLY", TODAY, category, ownerId, null,
                null, null, null));

        assertThat(internet.loan()).isNull();
        assertThat(internet.lastDueOn()).isNull();
        assertThat(internet.active()).isTrue();
    }

    @Test
    void inconsistent_loans_are_refused() {
        var variable = new ChargeRequest("Prêt", 30_000, "VARIABLE", "MONTHLY", TODAY, category, ownerId, null,
                null, null, new LoanRequest(0));
        var yearly = new ChargeRequest("Prêt", 30_000, "FIXED", "YEARLY", TODAY, category, ownerId, null, null,
                null, new LoanRequest(0));
        var income = new ChargeRequest("Prêt", 30_000, "FIXED", "MONTHLY", TODAY, category, ownerId, null, "INCOME",
                null, new LoanRequest(0));

        assertThat(failure(() -> add(variable)).code()).isEqualTo(400);
        assertThat(failure(() -> add(yearly)).code()).isEqualTo(400);
        assertThat(failure(() -> add(income)).code()).isEqualTo(400);
        assertThat(failure(() -> add(loan(30_000, 30_001L, TODAY, null))).code()).isEqualTo(400);
        assertThat(failure(() -> add(loan(30_000, 0L, TODAY.plusDays(2), TODAY.plusDays(1)))).code())
                .isEqualTo(400);
    }

    private ChargeRequest loan(long cents, long insurance, LocalDate firstDueOn, LocalDate lastDueOn) {
        return new ChargeRequest("Prêt immobilier", cents, "FIXED", "MONTHLY", firstDueOn, category, ownerId, null,
                null, lastDueOn, new LoanRequest(insurance));
    }

    private ChargeResponse add(ChargeRequest request) {
        return http.retrieve(HttpRequest.POST(charges(), request).bearerAuth(owner), ChargeResponse.class);
    }

    private List<LocalDate> forecastDates(LocalDate from, LocalDate to) {
        return http.retrieve(HttpRequest.GET(charges() + "/forecast?from=" + from + "&to=" + to).bearerAuth(owner),
                Argument.listOf(ForecastResponse.class)).stream().map(ForecastResponse::dueOn).toList();
    }

    /** Montants des dépenses du mois, en centimes. */
    @SuppressWarnings("unchecked")
    private List<Integer> expenseCents() {
        Map<String, Object> month = http.retrieve(HttpRequest.GET("/api/households/" + household + "/budget/"
                + MONTH).bearerAuth(owner), Argument.mapOf(String.class, Object.class));
        return ((List<Map<String, Object>>) month.get("expenses")).stream()
                .map(expense -> (Integer) expense.get("amountCents")).toList();
    }

    private String charges() {
        return "/api/households/" + household + "/charges";
    }
}
