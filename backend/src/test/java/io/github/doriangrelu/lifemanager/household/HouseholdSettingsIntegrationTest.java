package io.github.doriangrelu.lifemanager.household;

import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HouseholdSettingsIntegrationTest extends IntegrationTestSupport {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void only_the_owner_turns_income_tracking_on_and_off() {
        var api = new HouseholdApi(client.toBlocking());
        var owner = token("Alice");
        var guest = token("Bob");
        var household = api.create(owner, "Maison").id();
        api.join(guest, api.issueInvitation(owner, household).code());

        assertThat(api.get(guest, household).incomeTracking()).isFalse();
        api.trackIncome(owner, household, true);
        assertThat(api.get(guest, household).incomeTracking()).isTrue();
        assertThat(failure(() -> api.trackIncome(guest, household, false)).code()).isEqualTo(403);
        assertThat(failure(() -> api.trackIncome(token("Eve"), household, false)).code()).isEqualTo(404);
        api.trackIncome(owner, household, false);
        assertThat(api.get(owner, household).incomeTracking()).isFalse();
    }

    private String token(String name) {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", name);
        return token;
    }
}
