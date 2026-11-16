package io.github.doriangrelu.lifemanager.platform.infrastructure.security.token;

import io.github.doriangrelu.lifemanager.platform.infrastructure.security.config.IntrospectionSettings;
import io.github.doriangrelu.lifemanager.platform.infrastructure.security.event.UserAuthenticated;
import io.github.doriangrelu.lifemanager.platform.infrastructure.security.introspection.Introspection;
import io.github.doriangrelu.lifemanager.platform.infrastructure.security.introspection.IntrospectionCache;
import io.github.doriangrelu.lifemanager.platform.infrastructure.security.introspection.KeycloakIntrospectionClient;
import io.github.doriangrelu.lifemanager.platform.infrastructure.security.user.AuthenticatedUser;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.validator.TokenValidator;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Valide le jeton « Bearer » de chaque requête en le traitant comme opaque :
 * pas de décodage, seule l'introspection Keycloak fait foi.
 * Un jeton inactif ou destiné à une autre application donne une requête non authentifiée (401).
 */
@Singleton
class IntrospectionTokenValidator implements TokenValidator<HttpRequest<?>> {

    private final KeycloakIntrospectionClient keycloak;
    private final IntrospectionCache cache;
    private final IntrospectionSettings settings;
    private final ApplicationEventPublisher<UserAuthenticated> events;

    IntrospectionTokenValidator(KeycloakIntrospectionClient keycloak, IntrospectionCache cache,
                                IntrospectionSettings settings, ApplicationEventPublisher<UserAuthenticated> events) {
        this.keycloak = keycloak;
        this.cache = cache;
        this.settings = settings;
        this.events = events;
    }

    @Override
    public Publisher<Authentication> validateToken(String token, HttpRequest<?> request) {
        // L'introspection et les écouteurs sont bloquants (HTTP, base) : on quitte la boucle Netty.
        return Mono.fromCallable(() -> authenticate(token)).subscribeOn(Schedulers.boundedElastic());
    }

    /** Retourne l'utilisateur authentifié, ou {@code null} si le jeton n'est pas accepté. */
    private Authentication authenticate(String token) {
        var cached = cache.find(token);
        if (cached.isPresent()) {
            return AuthenticatedUser.toAuthentication(cached.get());
        }
        Introspection introspection = keycloak.introspect(token);
        if (!introspection.isValidFor(settings.audience())) {
            return null;
        }
        cache.remember(token, introspection);
        events.publishEvent(new UserAuthenticated(
                introspection.subject(), introspection.email(), introspection.displayName()));
        return AuthenticatedUser.toAuthentication(introspection);
    }
}
