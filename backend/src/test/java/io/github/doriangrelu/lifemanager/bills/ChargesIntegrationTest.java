package io.github.doriangrelu.lifemanager.bills;

import io.github.doriangrelu.lifemanager.bills.application.port.in.AdvanceChargeSchedules;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ChargeRequest;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ChargeResponse;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ConfirmRequest;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.InvoiceResponse;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.OccurrenceResponse;
import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Charges récurrentes de bout en bout : échéances, dépenses du budget, rappels, factures. */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChargesIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));
    private static final String MONTH = TODAY.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    private static final byte[] PDF = "%PDF-1.4\n%%EOF\n".getBytes(StandardCharsets.US_ASCII);

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    AdvanceChargeSchedules advanceSchedules;

    BlockingHttpClient http;
    HouseholdApi households;

    @BeforeAll
    void setUp() {
        http = client.toBlocking();
        households = new HouseholdApi(http);
    }

    @Test
    void a_fixed_charge_due_today_becomes_an_expense_and_plans_the_next_one() {
        var home = newHome();

        var internet = add(home, request(home, "Internet", 3999, "FIXED", TODAY));

        assertThat(internet.next().dueOn()).isEqualTo(TODAY.plusMonths(1));
        assertThat(internet.next().status()).isEqualTo("UPCOMING");
        assertThat(expenses(home)).containsExactly(Map.entry("internet", 3999));
        assertThat(occurrences(home, internet.id())).extracting(OccurrenceResponse::status)
                .containsExactly("UPCOMING", "PAID");
        // Le budget sait quelle part des dépenses vient des récurrents (pour le reste à vivre).
        assertThat(recurringCents(home, "/budget/" + MONTH)).isEqualTo(3999);
        assertThat(recurringCents(home, "/report?from=" + MONTH + "&to=" + MONTH)).isEqualTo(3999);
    }

    private int recurringCents(Home home, String path) {
        Map<String, Object> view = http.retrieve(HttpRequest.GET("/api/households/" + home.household + path)
                .bearerAuth(home.owner), Argument.mapOf(String.class, Object.class));
        return (Integer) view.get("recurringExpenseCents");
    }

    @Test
    void the_daily_run_never_pays_twice() {
        var home = newHome();
        add(home, request(home, "Internet", 3999, "FIXED", TODAY));

        advanceSchedules.advanceAll();
        advanceSchedules.advanceAll();

        assertThat(expenses(home)).hasSize(1);
    }

    @Test
    void a_variable_charge_waits_for_its_amount_with_a_reminder() {
        var home = newHome();
        var power = add(home, request(home, "Électricité", 8500, "VARIABLE", TODAY));
        assertThat(power.next().status()).isEqualTo("TO_CONFIRM");
        assertThat(reminderKinds(home)).containsExactly("CHARGE_TO_CONFIRM");
        assertThat(expenses(home)).isEmpty();

        confirm(home, power.id(), power.next().id(), 9240);

        assertThat(expenses(home)).containsExactly(Map.entry("électricité", 9240));
        assertThat(reminderKinds(home)).isEmpty(); // la suivante est dans un mois
        assertThat(failure(() -> confirm(home, power.id(), power.next().id(), 1)).code()).isEqualTo(409);
    }

    @Test
    void a_close_due_date_is_reminded_and_a_skipped_one_is_not_paid() {
        var home = newHome();
        var insurance = add(home, request(home, "Assurance", 2500, "FIXED", TODAY.plusDays(3)));
        assertThat(reminderKinds(home)).containsExactly("CHARGE");

        http.exchange(HttpRequest.POST(occurrence(home, insurance.id(), insurance.next().id()) + "/skip", "")
                .bearerAuth(home.owner));

        assertThat(occurrences(home, insurance.id())).extracting(OccurrenceResponse::status)
                .containsExactly("UPCOMING", "SKIPPED");
        assertThat(reminderKinds(home)).isEmpty();
        assertThat(expenses(home)).isEmpty();
    }

    @Test
    void a_terminated_charge_has_no_next_occurrence() {
        var home = newHome();
        var gym = add(home, request(home, "Salle de sport", 3000, "FIXED", TODAY.plusDays(2)));

        http.exchange(HttpRequest.DELETE(charges(home) + "/" + gym.id()).bearerAuth(home.owner));

        var listed = list(home).getFirst();
        assertThat(listed.active()).isFalse();
        assertThat(listed.next()).isNull();
        assertThat(reminderKinds(home)).isEmpty();
    }

    @Test
    void invoices_are_attached_to_an_occurrence() {
        var home = newHome();
        var internet = add(home, request(home, "Internet", 3999, "FIXED", TODAY.plusDays(5)));
        var base = occurrence(home, internet.id(), internet.next().id()) + "/invoices";

        var body = MultipartBody.builder().addPart("file", "facture.pdf", MediaType.APPLICATION_PDF_TYPE, PDF).build();
        var invoice = http.retrieve(HttpRequest.POST(base, body).contentType(MediaType.MULTIPART_FORM_DATA_TYPE)
                .bearerAuth(home.owner), InvoiceResponse.class);

        assertThat(invoice.mediaType()).isEqualTo("application/pdf");
        assertThat(http.retrieve(HttpRequest.GET(base + "/" + invoice.id() + "/content").bearerAuth(home.owner),
                byte[].class)).isEqualTo(PDF);
        assertThat(list(home).getFirst().next().invoiceCount()).isEqualTo(1);
        http.exchange(HttpRequest.DELETE(base + "/" + invoice.id()).bearerAuth(home.owner));
        assertThat(http.retrieve(HttpRequest.GET(base).bearerAuth(home.owner),
                Argument.listOf(InvoiceResponse.class))).isEmpty();
    }

    @Test
    void invalid_charges_and_strangers_are_refused() {
        var home = newHome();
        var stranger = newUser();

        assertThat(failure(() -> add(home, request(home, "Internet", 3999, "FIXED", TODAY.minusDays(1)))).code())
                .isEqualTo(400);
        var foreignPayer = new ChargeRequest("Internet", 3999, "FIXED", "MONTHLY", TODAY, home.category,
                UUID.randomUUID(), null, null);
        assertThat(failure(() -> add(home, foreignPayer)).code()).isEqualTo(400);
        assertThat(failure(() -> http.retrieve(HttpRequest.GET(charges(home)).bearerAuth(stranger))).code())
                .isEqualTo(404);
    }

    /** Un foyer avec son membre, son identifiant et une catégorie du budget. */
    private record Home(String owner, UUID ownerId, UUID household, UUID category) {
    }

    private Home newHome() {
        var owner = newUser();
        var ownerId = UUID.fromString(String.valueOf(http.retrieve(HttpRequest.GET("/api/whoami").bearerAuth(owner),
                Map.class).get("userId")));
        var household = households.create(owner, "Maison").id();
        List<Map<String, Object>> categories = http.retrieve(HttpRequest.GET("/api/households/" + household
                + "/expense-categories").bearerAuth(owner), Argument.listOf(Argument.mapOf(String.class, Object.class)));
        var category = UUID.fromString(String.valueOf(categories.getFirst().get("id")));
        return new Home(owner, ownerId, household, category);
    }

    private static ChargeRequest request(Home home, String name, long cents, String kind, LocalDate firstDueOn) {
        return new ChargeRequest(name, cents, kind, "MONTHLY", firstDueOn, home.category, home.ownerId, null, null);
    }

    private ChargeResponse add(Home home, ChargeRequest request) {
        return http.retrieve(HttpRequest.POST(charges(home), request).bearerAuth(home.owner), ChargeResponse.class);
    }

    private List<ChargeResponse> list(Home home) {
        return http.retrieve(HttpRequest.GET(charges(home)).bearerAuth(home.owner),
                Argument.listOf(ChargeResponse.class));
    }

    private List<OccurrenceResponse> occurrences(Home home, UUID charge) {
        return http.retrieve(HttpRequest.GET(charges(home) + "/" + charge + "/occurrences").bearerAuth(home.owner),
                Argument.listOf(OccurrenceResponse.class));
    }

    private OccurrenceResponse confirm(Home home, UUID charge, UUID occurrence, long cents) {
        return http.retrieve(HttpRequest.POST(occurrence(home, charge, occurrence) + "/confirm",
                new ConfirmRequest(cents)).bearerAuth(home.owner), OccurrenceResponse.class);
    }

    /** Dépenses du mois : libellé → montant en centimes. */
    @SuppressWarnings("unchecked")
    private List<Map.Entry<String, Integer>> expenses(Home home) {
        Map<String, Object> month = http.retrieve(HttpRequest.GET("/api/households/" + home.household + "/budget/"
                + MONTH).bearerAuth(home.owner), Argument.mapOf(String.class, Object.class));
        return ((List<Map<String, Object>>) month.get("expenses")).stream()
                .map(expense -> Map.entry(String.valueOf(expense.get("label")), (Integer) expense.get("amountCents")))
                .toList();
    }

    private List<String> reminderKinds(Home home) {
        List<Map<String, Object>> reminders = http.retrieve(HttpRequest.GET("/api/households/" + home.household
                + "/reminders").bearerAuth(home.owner), Argument.listOf(Argument.mapOf(String.class, Object.class)));
        return reminders.stream().map(reminder -> String.valueOf(reminder.get("kind"))).toList();
    }

    private static String charges(Home home) {
        return "/api/households/" + home.household + "/charges";
    }

    private static String occurrence(Home home, UUID charge, UUID occurrence) {
        return charges(home) + "/" + charge + "/occurrences/" + occurrence;
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
