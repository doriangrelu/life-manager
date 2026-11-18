package io.github.doriangrelu.lifemanager.household;

import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.HouseholdSummaryResponse;
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
class HouseholdIntegrationTest extends IntegrationTestSupport {

    @Inject
    @Client("/")
    HttpClient client;

    HouseholdApi api;

    @BeforeAll
    void setUp() {
        api = new HouseholdApi(client.toBlocking());
    }

    @Test
    void a_user_creates_households_and_finds_them() {
        var token = newUser("Fanny");

        var home = api.create(token, "Maison");
        var family = api.create(token, "Maison de famille");

        assertThat(api.list(token)).containsExactly(home, family);
        var details = api.get(token, home.id());
        assertThat(details.name()).isEqualTo("maison");
        assertThat(details.members()).extracting(MemberResponse::displayName).containsExactly("Fanny");
        assertThat(details.activeInvitation()).isNull();
    }

    @Test
    void a_user_without_household_gets_an_empty_list() {
        assertThat(api.list(newUser("Gaston"))).isEmpty();
    }

    @Test
    void an_invalid_name_is_refused() {
        var token = newUser("Hugo");

        assertThat(failure(() -> api.create(token, " ")).code()).isEqualTo(400);
        assertThat(failure(() -> api.create(token, "a".repeat(101))).code()).isEqualTo(400);
    }

    @Test
    void a_household_is_invisible_to_non_members() {
        var owner = newUser("Inès");
        var stranger = newUser("Jules");
        var household = api.create(owner, "Chez Inès");

        assertThat(failure(() -> api.get(stranger, household.id())).code()).isEqualTo(404);
        assertThat(failure(() -> api.issueInvitation(stranger, household.id())).code()).isEqualTo(404);
        assertThat(failure(() -> api.get(stranger, UUID.randomUUID())).code()).isEqualTo(404);
        assertThat(api.list(stranger)).isEmpty();
    }

    @Test
    void a_user_cannot_belong_to_more_than_ten_households() {
        var token = newUser("Karim");
        IntStream.rangeClosed(1, 10).forEach(i -> api.create(token, "Foyer " + i));

        var error = failure(() -> api.create(token, "Foyer 11"));

        assertThat(error.code()).isEqualTo(409);
        assertThat(api.list(token)).extracting(HouseholdSummaryResponse::name).hasSize(10);
    }

    private String newUser(String name) {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, name.toLowerCase() + "-" + UUID.randomUUID() + "@example.org", name);
        return token;
    }
}
