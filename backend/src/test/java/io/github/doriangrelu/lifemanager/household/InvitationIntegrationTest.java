package io.github.doriangrelu.lifemanager.household;

import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.MemberResponse;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;
import java.util.stream.IntStream;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InvitationIntegrationTest extends IntegrationTestSupport {

    @Inject
    @Client("/")
    HttpClient client;

    HouseholdApi api;

    @BeforeAll
    void setUp() {
        api = new HouseholdApi(client.toBlocking());
    }

    @Test
    void an_invited_user_joins_and_the_code_cannot_be_reused() {
        var alice = newUser("Alice");
        var bob = newUser("Bob");
        var carol = newUser("Carol");
        var household = api.create(alice, "Maison");
        var invitation = api.issueInvitation(alice, household.id());

        assertThat(api.get(alice, household.id()).activeInvitation()).isEqualTo(invitation);
        assertThat(api.join(bob, invitation.code())).isEqualTo(household);

        var members = api.get(bob, household.id()).members();
        assertThat(members).extracting(MemberResponse::displayName).containsExactly("Alice", "Bob");
        var reuse = failure(() -> api.join(carol, invitation.code()));
        assertThat(reuse.code()).isEqualTo(400);
        assertThat(reuse.getResponse().getBody(String.class)).hasValueSatisfying(
                body -> assertThat(body).contains("Ce code d'invitation n'est pas valide"));
    }

    @Test
    void a_new_code_replaces_the_previous_one() {
        var owner = newUser("Denis");
        var guest = newUser("Elsa");
        var household = api.create(owner, "Chez Denis");
        var first = api.issueInvitation(owner, household.id());
        var second = api.issueInvitation(owner, household.id());

        assertThat(failure(() -> api.join(guest, first.code())).code()).isEqualTo(400);
        assertThat(api.join(guest, second.code())).isEqualTo(household);
    }

    @Test
    void a_member_using_a_code_of_their_household_is_refused_and_the_code_stays_valid() {
        var owner = newUser("Farid");
        var guest = newUser("Gina");
        var household = api.create(owner, "Chez Farid");
        var invitation = api.issueInvitation(owner, household.id());

        assertThat(failure(() -> api.join(owner, invitation.code())).code()).isEqualTo(409);
        assertThat(api.join(guest, invitation.code())).isEqualTo(household);
    }

    @Test
    void too_many_attempts_are_blocked() {
        var guesser = newUser("Hacker");
        IntStream.range(0, 10).forEach(i ->
                assertThat(failure(() -> api.join(guesser, "ABCDEFGH")).code()).isEqualTo(400));

        assertThat(failure(() -> api.join(guesser, "ABCDEFGH")).code()).isEqualTo(429);
    }

    @Test
    void a_name_changed_in_keycloak_is_shown_to_the_other_members() {
        var aliceSubject = UUID.randomUUID().toString();
        KEYCLOAK.respond("token-ivy-1", aliceSubject, "ivy@example.org", "Ivy", "life-manager-api");
        var bob = newUser("Jack");
        var household = api.create("token-ivy-1", "Coloc");
        api.join(bob, api.issueInvitation("token-ivy-1", household.id()).code());

        // Ivy change de nom dans Keycloak puis se reconnecte (nouveau jeton).
        KEYCLOAK.respond("token-ivy-2", aliceSubject, "ivy@example.org", "Ivy Martin", "life-manager-api");
        api.list("token-ivy-2");

        assertThat(api.get(bob, household.id()).members())
                .extracting(MemberResponse::displayName).containsExactly("Ivy Martin", "Jack");
    }

    private String newUser(String name) {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, name.toLowerCase() + "-" + UUID.randomUUID() + "@example.org", name);
        return token;
    }
}
