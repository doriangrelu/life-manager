package io.github.doriangrelu.lifemanager.platform.infrastructure.web.error;

import io.github.doriangrelu.lifemanager.platform.infrastructure.security.exception.IdentityProviderUnavailableException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Keycloak indisponible → 503 Service Unavailable (la cause est journalisée, pas renvoyée). */
@Singleton
@Produces
class IdentityProviderUnavailableExceptionHandler extends StatusExceptionHandler<IdentityProviderUnavailableException> {

    private static final Logger LOG = LoggerFactory.getLogger(IdentityProviderUnavailableExceptionHandler.class);

    IdentityProviderUnavailableExceptionHandler(ErrorResponseProcessor<?> errorResponses) {
        super(errorResponses, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public HttpResponse<?> handle(HttpRequest request, IdentityProviderUnavailableException exception) {
        LOG.error("Introspection Keycloak impossible", exception);
        return super.handle(request, exception);
    }
}
