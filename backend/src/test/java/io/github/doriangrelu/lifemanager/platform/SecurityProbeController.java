package io.github.doriangrelu.lifemanager.platform;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;
import io.github.doriangrelu.lifemanager.platform.infrastructure.security.user.AuthenticatedUser;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.SerdeImport;

/**
 * Contrôleur de test uniquement : expose l'utilisateur authentifié et déclenche chaque type d'erreur métier.
 */
@SerdeImport(AuthenticatedUser.class)
@Controller("/test-probe")
@Secured(SecurityRule.IS_AUTHENTICATED)
class SecurityProbeController {

    @Get("/me")
    AuthenticatedUser me(Authentication authentication) {
        return AuthenticatedUser.from(authentication);
    }

    @Get("/invalid")
    void invalid() {
        throw new InvalidValueException("valeur invalide");
    }

    @Get("/not-found")
    void notFound() {
        throw new NotFoundException("introuvable") {
        };
    }

    @Get("/conflict")
    void conflict() {
        throw new ConflictException("conflit") {
        };
    }
}
