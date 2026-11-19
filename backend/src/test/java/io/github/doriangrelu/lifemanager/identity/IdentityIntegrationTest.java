package io.github.doriangrelu.lifemanager.identity;

import io.github.doriangrelu.lifemanager.identity.contract.event.UserProfileChangedV1;
import io.github.doriangrelu.lifemanager.identity.contract.event.UserRegisteredV1;
import io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.dto.UserProfileResponse;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IdentityIntegrationTest extends IntegrationTestSupport {

    /** Mémorise les événements de contrat publiés par identity, comme le ferait un autre domaine. */
    @Singleton
    static class ContractEvents {

        final List<Object> received = new CopyOnWriteArrayList<>();

        @EventListener
        void on(UserRegisteredV1 event) {
            received.add(event);
        }

        @EventListener
        void on(UserProfileChangedV1 event) {
            received.add(event);
        }
    }

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    ContractEvents contractEvents;

    @Test
    void first_call_creates_the_profile_and_announces_it() {
        var subject = KEYCLOAK.activeToken("token-dave", "Dave@Example.org", "Dave");

        var profile = whoami("token-dave");

        assertThat(profile).isEqualTo(new UserProfileResponse(UUID.fromString(subject), "dave@example.org", "Dave",
                false, "2026-11-19"));
        assertThat(eventsAbout(subject)).singleElement().isInstanceOf(UserRegisteredV1.class);
    }

    @Test
    void name_change_in_keycloak_updates_the_profile_and_is_announced() {
        var subject = KEYCLOAK.activeToken("token-erin-1", "erin@example.org", "Erin");
        whoami("token-erin-1");
        // Nouveau jeton (nouvelle connexion) après un changement de nom dans Keycloak.
        KEYCLOAK.respond("token-erin-2", subject, "erin@example.org", "Erin Dupont", "life-manager-api");

        var profile = whoami("token-erin-2");

        assertThat(profile.displayName()).isEqualTo("Erin Dupont");
        assertThat(eventsAbout(subject)).hasSize(2).last().isInstanceOf(UserProfileChangedV1.class);
    }

    @Test
    void whoami_requires_an_active_token() {
        var error = catchThrowableOfType(HttpClientResponseException.class, () -> whoami("token-inactive"));

        assertThat(error.code()).isEqualTo(401);
    }

    private UserProfileResponse whoami(String token) {
        return client.toBlocking().retrieve(HttpRequest.GET("/api/whoami").bearerAuth(token), UserProfileResponse.class);
    }

    private List<Object> eventsAbout(String subject) {
        var userId = UUID.fromString(subject);
        return contractEvents.received.stream()
                .filter(event -> switch (event) {
                    case UserRegisteredV1 registered -> registered.userId().equals(userId);
                    case UserProfileChangedV1 changed -> changed.userId().equals(userId);
                    default -> false;
                })
                .toList();
    }
}
