package io.github.doriangrelu.lifemanager.platform.infrastructure.web.error;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ForbiddenException;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

/** Action réservée (ex. au propriétaire du foyer) → 403 Forbidden. */
@Singleton
@Produces
class ForbiddenExceptionHandler extends StatusExceptionHandler<ForbiddenException> {

    ForbiddenExceptionHandler(ErrorResponseProcessor<?> errorResponses) {
        super(errorResponses, HttpStatus.FORBIDDEN);
    }
}
