package io.github.doriangrelu.lifemanager.identity.infrastructure.out.keycloak;

import io.github.doriangrelu.lifemanager.identity.application.port.out.LoginAccounts;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.UpstreamFailureException;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientException;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Supprime un compte de connexion par l'API d'administration de Keycloak, avec le compte de service
 * du client de l'API (rôle {@code manage-users} du realm, rien d'autre). Appel bloquant.
 */
@Singleton
class KeycloakLoginAccounts implements LoginAccounts {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakLoginAccounts.class);
    private static final Argument<Map<String, Object>> JSON_OBJECT = Argument.mapOf(String.class, Object.class);
    private static final String FAILURE = "Vos données sont effacées, mais la suppression de votre compte de connexion "
            + "a échoué. Reconnectez-vous puis recommencez, ou écrivez à doriangrelu@gmail.com.";

    private final HttpClient keycloak;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    KeycloakLoginAccounts(@Client("keycloak") HttpClient keycloak,
                          @Value("${life-manager.keycloak-realm:life-manager}") String realm,
                          @Value("${security.introspection.client-id}") String clientId,
                          @Value("${security.introspection.client-secret}") String clientSecret) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public void delete(UserId id) {
        try {
            var request = HttpRequest.DELETE("/admin/realms/" + realm + "/users/" + id.value())
                    .bearerAuth(adminToken());
            keycloak.toBlocking().exchange(request);
        } catch (HttpClientResponseException e) {
            if (e.getStatus() == HttpStatus.NOT_FOUND) {
                return; // déjà supprimé
            }
            LOG.warn("Suppression du compte Keycloak refusée : {}", e.getStatus());
            throw new UpstreamFailureException(FAILURE);
        } catch (HttpClientException e) {
            LOG.warn("Keycloak injoignable pour supprimer un compte : {}", e.getMessage());
            throw new UpstreamFailureException(FAILURE);
        }
    }

    // Jeton du compte de service du client de l'API.
    private String adminToken() {
        var request = HttpRequest.POST("/realms/" + realm + "/protocol/openid-connect/token",
                        Map.of("grant_type", "client_credentials"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .basicAuth(clientId, clientSecret);
        var token = keycloak.toBlocking().retrieve(request, JSON_OBJECT).get("access_token");
        if (!(token instanceof String value)) {
            throw new UpstreamFailureException(FAILURE);
        }
        return value;
    }
}
