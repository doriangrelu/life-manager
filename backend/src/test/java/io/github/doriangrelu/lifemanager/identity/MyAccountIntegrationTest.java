package io.github.doriangrelu.lifemanager.identity;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.MemberResponse;
import io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.dto.UserProfileResponse;
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
import java.util.Map;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Compte de l'utilisateur : conditions acceptées, export de ce qu'il a saisi, suppression du compte. */
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyAccountIntegrationTest extends IntegrationTestSupport {

    private static final String TODAY = LocalDate.now(ZoneId.of("Europe/Paris")).toString();

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
    void terms_must_be_accepted_in_their_current_version() {
        var user = newUser(UUID.randomUUID() + "@example.org").token;

        assertThat(whoami(user).termsAccepted()).isFalse();
        assertThat(failure(() -> acceptTerms(user, "2020-01-01")).code()).isEqualTo(400);
        acceptTerms(user, whoami(user).termsVersion());

        assertThat(whoami(user).termsAccepted()).isTrue();
    }

    @Test
    void export_holds_what_the_user_entered_and_nothing_from_others() {
        var me = newUser("moi-" + UUID.randomUUID() + "@example.org");
        var other = newUser("autre-" + UUID.randomUUID() + "@example.org");
        var home = households.create(me.token, "Maison").id();
        var code = households.issueInvitation(me.token, home).code();
        households.join(other.token, code);
        var category = post(me.token, home, "/expense-categories", Map.of("name", "Jardin")).get("id");
        post(me.token, home, "/expenses", expense(category, me.id, "Graines"));
        post(other.token, home, "/expenses", expense(category, other.id, "Pas à moi"));
        post(me.token, home, "/personal-reminders", Map.of("title", "Ramonage", "dueOn", TODAY,
                "recurrence", "YEARLY", "notice", "ONE_WEEK"));

        var raw = http.retrieve(HttpRequest.GET("/api/me/export").bearerAuth(me.token), String.class);

        // Libellés et noms enregistrés normalisés (minuscules).
        assertThat(raw).contains("graines", "jardin", "Ramonage", "propriétaire", me.email)
                .doesNotContain("pas à moi", other.email, code, me.id.toString());
    }

    @Test
    void a_second_export_within_a_minute_is_refused() {
        var me = newUser(UUID.randomUUID() + "@example.org");
        http.retrieve(HttpRequest.GET("/api/me/export").bearerAuth(me.token), String.class);

        assertThat(failure(() -> http.retrieve(HttpRequest.GET("/api/me/export").bearerAuth(me.token), String.class))
                .code()).isEqualTo(429);
    }

    @Test
    void deleting_the_account_hands_shared_homes_over_and_deletes_solo_ones() throws SQLException {
        var owner = newUser("partant-" + UUID.randomUUID() + "@example.org");
        var bob = newUser(UUID.randomUUID() + "@example.org");
        var shared = households.create(owner.token, "Maison").id();
        households.join(bob.token, households.issueInvitation(owner.token, shared).code());
        var solo = households.create(owner.token, "Seul").id();

        assertThat(failure(() -> deleteAccount(owner.token, "autre@example.org")).code()).isEqualTo(400);
        deleteAccount(owner.token, owner.email.toUpperCase());

        assertThat(KEYCLOAK.deletedUsers()).contains(owner.id.toString());
        var details = households.get(bob.token, shared);
        assertThat(details.ownerId()).isEqualTo(bob.id);
        assertThat(details.members()).extracting(MemberResponse::memberId).containsExactly(bob.id);
        assertThat(count("SELECT count(*) FROM household.household WHERE id = ?", solo)).isZero();
        assertThat(count("SELECT count(*) FROM identity.user_account WHERE id = ?", owner.id)).isZero();
    }

    private record User(String token, UUID id, String email) {
    }

    private UserProfileResponse whoami(String token) {
        return http.retrieve(HttpRequest.GET("/api/whoami").bearerAuth(token), UserProfileResponse.class);
    }

    private void acceptTerms(String token, String version) {
        http.exchange(HttpRequest.PUT("/api/me/terms", Map.of("version", version)).bearerAuth(token));
    }

    private void deleteAccount(String token, String email) {
        http.exchange(HttpRequest.DELETE("/api/me", Map.of("confirmEmail", email)).bearerAuth(token));
    }

    private Map<String, Object> post(String token, UUID household, String path, Map<String, ?> body) {
        return http.retrieve(HttpRequest.POST("/api/households/" + household + path, body).bearerAuth(token),
                Argument.mapOf(String.class, Object.class));
    }

    private static Map<String, Object> expense(Object category, UUID payer, String label) {
        return Map.of("amountCents", 2000, "categoryId", category, "spentOn", TODAY, "paidBy", payer,
                "label", label);
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

    private static User newUser(String email) {
        var token = "token-" + UUID.randomUUID();
        var subject = KEYCLOAK.activeToken(token, email, "Membre");
        return new User(token, UUID.fromString(subject), email);
    }
}
