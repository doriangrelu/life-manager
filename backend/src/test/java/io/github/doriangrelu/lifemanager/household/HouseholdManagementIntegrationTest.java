package io.github.doriangrelu.lifemanager.household;

import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.MemberResponse;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Gestion du foyer de bout en bout : propriétaire, invitations, retrait, départ, renommage, suppression complète. */
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HouseholdManagementIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Europe/Paris"));
    private static final byte[] PDF = "%PDF-1.4\n%%EOF\n".getBytes(StandardCharsets.US_ASCII);
    /** Routes qu'un ancien membre ne doit plus pouvoir lire. */
    private static final List<String> MEMBER_ROUTES = List.of("", "/expense-categories", "/charges", "/vehicles",
            "/shopping-lists", "/reminders");

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
    void only_the_owner_invites_renames_and_hands_over() {
        var home = newHome();

        assertThat(households.get(home.owner, home.id).ownerId()).isEqualTo(home.ownerId);
        assertThat(households.get(home.owner, home.id).activeInvitation()).isNull();
        var code = households.issueInvitation(home.owner, home.id).code();
        assertThat(households.get(home.owner, home.id).activeInvitation()).isNotNull();
        assertThat(households.get(home.bob, home.id).activeInvitation()).as("code caché aux membres").isNull();
        assertThat(failure(() -> households.issueInvitation(home.bob, home.id)).code()).isEqualTo(403);
        assertThat(failure(() -> households.cancelInvitation(home.bob, home.id)).code()).isEqualTo(403);
        assertThat(failure(() -> households.rename(home.bob, home.id, "Chez Bob")).code()).isEqualTo(403);
        assertThat(failure(() -> households.removeMember(home.bob, home.id, home.ownerId)).code()).isEqualTo(403);

        households.cancelInvitation(home.owner, home.id);
        assertThat(failure(() -> households.join(newUser(), code)).code()).isEqualTo(400);
        assertThat(households.rename(home.owner, home.id, "Maison Grelu").name()).isEqualTo("maison grelu");

        households.transferOwnership(home.owner, home.id, home.bobId);
        assertThat(households.get(home.owner, home.id).ownerId()).isEqualTo(home.bobId);
        assertThat(failure(() -> households.issueInvitation(home.owner, home.id)).code()).isEqualTo(403);
    }

    @Test
    void a_removed_member_loses_access_everywhere_and_his_charges_go_to_the_owner() {
        var home = newHome();
        var category = firstCategory(home);
        post(home.bob, home.id, "/expenses", expense(category, home.bobId));
        var charge = post(home.owner, home.id, "/charges", charge(category, home.bobId, home.bobId));

        households.removeMember(home.owner, home.id, home.bobId);

        for (var route : MEMBER_ROUTES) {
            assertThat(failure(() -> get(home.bob, home.id, route)).code()).as(route).isEqualTo(404);
        }
        assertThat(households.list(home.bob)).isEmpty();
        assertThat(households.get(home.owner, home.id).members()).extracting(MemberResponse::memberId)
                .containsExactly(home.ownerId);
        // La dépense de Bob reste ; sa charge est désormais payée par le propriétaire, pour tout le foyer.
        assertThat(count("budget", "SELECT count(*) FROM expense WHERE household_id = ?", home.id)).isEqualTo(2);
        var reassigned = getList(home.owner, home.id, "/charges").getFirst();
        assertThat(reassigned.get("id")).isEqualTo(charge.get("id"));
        assertThat(reassigned.get("payerId")).isEqualTo(home.ownerId.toString());
        assertThat(reassigned.get("beneficiaryId")).isNull();
    }

    @Test
    void a_member_can_leave_but_not_the_owner() {
        var home = newHome();

        assertThat(failure(() -> households.leave(home.owner, home.id)).code()).isEqualTo(409);
        assertThat(failure(() -> households.removeMember(home.owner, home.id, home.ownerId)).code()).isEqualTo(409);
        households.leave(home.bob, home.id);

        assertThat(failure(() -> get(home.bob, home.id, "/shopping-lists")).code()).isEqualTo(404);
    }

    @Test
    void deleting_a_household_erases_every_row_and_file() throws IOException {
        var home = newHome();
        var category = firstCategory(home);
        post(home.owner, home.id, "/expenses", expense(category, home.ownerId));
        var charge = post(home.owner, home.id, "/charges", charge(category, home.ownerId, null));
        var occurrence = UUID.fromString(String.valueOf(((Map<?, ?>) charge.get("next")).get("id")));
        var filesBefore = files("bills");
        var invoice = MultipartBody.builder().addPart("file", "facture.pdf", MediaType.APPLICATION_PDF_TYPE, PDF).build();
        http.exchange(HttpRequest.POST(route(home.id, "/charges/" + charge.get("id") + "/occurrences/" + occurrence
                + "/invoices"), invoice).contentType(MediaType.MULTIPART_FORM_DATA_TYPE).bearerAuth(home.owner));
        post(home.owner, home.id, "/vehicles", Map.of("name", "Clio", "licensePlate", "AB-123-CD"));
        assertThat(files("bills")).isEqualTo(filesBefore + 1);

        assertThat(failure(() -> households.delete(home.owner, home.id, "pas le bon nom")).code()).isEqualTo(400);
        assertThat(failure(() -> households.delete(home.bob, home.id, "maison")).code()).isEqualTo(403);
        households.delete(home.owner, home.id, "  MAISON ");

        assertThat(failure(() -> households.get(home.owner, home.id)).code()).isEqualTo(404);
        assertThat(households.list(home.bob)).isEmpty();
        assertNothingLeft(home.id);
        assertThat(files("bills")).as("fichier de facture effacé").isEqualTo(filesBefore);
    }

    /** Aucune ligne du foyer dans aucun schéma. */
    private static void assertNothingLeft(UUID household) {
        for (var table : List.of("household.household", "household.membership", "household.invitation",
                "budget.expense", "budget.category", "budget.household_member", "bills.recurring_charge",
                "bills.charge_occurrence", "bills.household_member", "vehicle.vehicle", "vehicle.household_member",
                "shopping.shopping_list", "shopping.household_member", "reminder.deadline",
                "reminder.household_member")) {
            var column = table.equals("household.household") ? "id" : "household_id";
            assertThat(count(null, "SELECT count(*) FROM " + table + " WHERE " + column + " = ?", household))
                    .as(table).isZero();
        }
    }

    /** Un foyer avec son propriétaire et Bob, membre. */
    private record Home(UUID id, String owner, UUID ownerId, String bob, UUID bobId) {
    }

    private Home newHome() {
        var owner = newUser();
        var bob = newUser();
        var id = households.create(owner, "Maison").id();
        households.join(bob, households.issueInvitation(owner, id).code());
        households.cancelInvitation(owner, id);
        return new Home(id, owner, whoami(owner), bob, whoami(bob));
    }

    private UUID firstCategory(Home home) {
        return UUID.fromString(String.valueOf(getList(home.owner, home.id, "/expense-categories").getFirst().get("id")));
    }

    private static Map<String, Object> expense(UUID category, UUID payer) {
        var body = new HashMap<String, Object>();
        body.put("amountCents", 2000);
        body.put("categoryId", category);
        body.put("spentOn", TODAY.toString());
        body.put("paidBy", payer);
        body.put("label", "Essai");
        return body;
    }

    private static Map<String, Object> charge(UUID category, UUID payer, UUID beneficiary) {
        var body = new HashMap<String, Object>();
        body.put("name", "Internet");
        body.put("amountCents", 3999);
        body.put("amountKind", "FIXED");
        body.put("frequency", "MONTHLY");
        body.put("firstDueOn", TODAY.toString());
        body.put("budgetCategoryId", category);
        body.put("payerId", payer);
        body.put("beneficiaryId", beneficiary);
        return body;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String token, UUID household, String path, Map<String, ?> body) {
        return http.retrieve(HttpRequest.POST(route(household, path), body).bearerAuth(token), Map.class);
    }

    private Object get(String token, UUID household, String path) {
        return http.retrieve(HttpRequest.GET(route(household, path)).bearerAuth(token), String.class);
    }

    private List<Map<String, Object>> getList(String token, UUID household, String path) {
        return http.retrieve(HttpRequest.GET(route(household, path)).bearerAuth(token),
                Argument.listOf(Argument.mapOf(String.class, Object.class)));
    }

    private static String route(UUID household, String path) {
        return "/api/households/" + household + path;
    }

    private UUID whoami(String token) {
        return UUID.fromString(String.valueOf(http.retrieve(HttpRequest.GET("/api/whoami").bearerAuth(token), Map.class)
                .get("userId")));
    }

    /** Nombre de lignes, en interrogeant directement la base de test ({@code schema} : préfixe éventuel). */
    private static long count(String schema, String sql, UUID household) {
        var query = schema == null ? sql : sql.replace("FROM ", "FROM " + schema + ".");
        try (var connection = DriverManager.getConnection(postgres().getJdbcUrl(), postgres().getUsername(),
                postgres().getPassword()); var statement = connection.prepareStatement(query)) {
            statement.setObject(1, household);
            try (var result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static long files(String folder) throws IOException {
        var root = STORAGE_ROOT.resolve(folder);
        if (!Files.exists(root)) {
            return 0;
        }
        try (var walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile).count();
        }
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
