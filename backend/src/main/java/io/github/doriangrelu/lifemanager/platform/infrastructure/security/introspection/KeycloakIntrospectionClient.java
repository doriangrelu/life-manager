package io.github.doriangrelu.lifemanager.platform.infrastructure.security.introspection;

import io.github.doriangrelu.lifemanager.platform.infrastructure.security.config.IntrospectionSettings;
import io.github.doriangrelu.lifemanager.platform.infrastructure.security.exception.IdentityProviderUnavailableException;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientException;
import jakarta.inject.Singleton;

import java.util.Map;

/**
 * Appelle l'endpoint d'introspection de Keycloak (RFC 7662) pour savoir si un jeton est valide.
 * Appel bloquant : à utiliser hors de la boucle d'événements Netty.
 */
@Singleton
public class KeycloakIntrospectionClient {

    private static final Argument<Map<String, Object>> JSON_OBJECT = Argument.mapOf(String.class, Object.class);

    private final HttpClient keycloak;
    private final IntrospectionSettings settings;

    KeycloakIntrospectionClient(@Client("keycloak") HttpClient keycloak, IntrospectionSettings settings) {
        this.keycloak = keycloak;
        this.settings = settings;
    }

    /**
     * Demande à Keycloak ce qu'il sait du jeton.
     *
     * @throws IdentityProviderUnavailableException si Keycloak ne répond pas correctement
     */
    public Introspection introspect(String token) {
        var request = HttpRequest.POST(settings.path(), Map.of("token", token))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .basicAuth(settings.clientId(), settings.clientSecret());
        try {
            return Introspection.fromResponse(keycloak.toBlocking().retrieve(request, JSON_OBJECT));
        } catch (HttpClientException e) {
            throw new IdentityProviderUnavailableException(e);
        }
    }
}
