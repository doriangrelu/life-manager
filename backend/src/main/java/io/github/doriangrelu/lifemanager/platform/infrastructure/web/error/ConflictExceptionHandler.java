package io.github.doriangrelu.lifemanager.platform.infrastructure.web.error;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

/** Opération incompatible avec l'état actuel → 409 Conflict. */
@Singleton
@Produces
class ConflictExceptionHandler extends StatusExceptionHandler<ConflictException> {

    ConflictExceptionHandler(ErrorResponseProcessor<?> errorResponses) {
        super(errorResponses, HttpStatus.CONFLICT);
    }
}
