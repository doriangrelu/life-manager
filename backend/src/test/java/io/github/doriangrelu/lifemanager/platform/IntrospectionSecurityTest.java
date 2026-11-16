package io.github.doriangrelu.lifemanager.platform;

import io.github.doriangrelu.lifemanager.platform.infrastructure.security.event.UserAuthenticated;
import io.github.doriangrelu.lifemanager.platform.infrastructure.security.user.AuthenticatedUser;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntrospectionSecurityTest extends IntegrationTestSupport {

    /** Mémorise les événements UserAuthenticated publiés par la plateforme. */
    @Singleton
    static class AuthenticatedUsers {

        final List<UserAuthenticated> received = new CopyOnWriteArrayList<>();

        @EventListener
        void on(UserAuthenticated event) {
            received.add(event);
        }
    }

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    AuthenticatedUsers authenticatedUsers;

    @AfterEach
    void restoreKeycloak() {
        KEYCLOAK.setAvailable(true);
    }

    @Test
    void active_token_authenticates_the_user() {
        var subject = KEYCLOAK.activeToken("token-alice", "alice@example.org", "Alice");

        var me = callMe("token-alice");

        assertThat(me).isEqualTo(new AuthenticatedUser(subject, "alice@example.org", "Alice"));
    }

    @Test
    void unknown_token_is_rejected() {
        assertThat(statusOfMe("token-unknown")).isEqualTo(401);
    }

    @Test
    void token_for_another_audience_is_rejected() {
        KEYCLOAK.respond("token-other-app", "c0ffee00-0000-0000-0000-000000000001", "x@example.org", "X", "other-app");

        assertThat(statusOfMe("token-other-app")).isEqualTo(401);
    }

    @Test
    void unavailable_keycloak_gives_503() {
        KEYCLOAK.setAvailable(false);

        assertThat(statusOfMe("token-while-down")).isEqualTo(503);
    }

    @Test
    void valid_token_is_introspected_once_then_cached() {
        KEYCLOAK.activeToken("token-bob", "bob@example.org", "Bob");
        int callsBefore = KEYCLOAK.calls();

        callMe("token-bob");
        callMe("token-bob");

        assertThat(KEYCLOAK.calls() - callsBefore).isEqualTo(1);
        assertThat(authenticatedUsers.received)
                .filteredOn(event -> event.email().equals("bob@example.org"))
                .hasSize(1);
    }

    private AuthenticatedUser callMe(String token) {
        return client.toBlocking().retrieve(
                HttpRequest.GET("/test-probe/me").bearerAuth(token), AuthenticatedUser.class);
    }

    private int statusOfMe(String token) {
        var error = catchThrowableOfType(HttpClientResponseException.class, () -> callMe(token));
        return error.code();
    }
}
