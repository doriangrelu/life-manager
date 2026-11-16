package io.github.doriangrelu.lifemanager.platform.infrastructure.security.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;

/**
 * Réglages de l'introspection Keycloak (section {@code security.introspection} de la configuration).
 *
 * @param path         chemin de l'endpoint d'introspection du realm
 * @param clientId     client confidentiel autorisé à introspecter
 * @param clientSecret secret de ce client
 * @param audience     audience que le jeton doit viser pour être accepté
 * @param cacheTtl     durée pendant laquelle un jeton valide n'est pas revérifié
 */
@ConfigurationProperties("security.introspection")
public record IntrospectionSettings(
        @NotBlank String path,
        @NotBlank String clientId,
        @NotBlank String clientSecret,
        @NotBlank String audience,
        @NotNull Duration cacheTtl) {
}
