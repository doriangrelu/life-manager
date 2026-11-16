package io.github.doriangrelu.lifemanager.platform.infrastructure.web.error;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

/** Donnée métier invalide → 400 Bad Request. */
@Singleton
@Produces
class InvalidValueExceptionHandler extends StatusExceptionHandler<InvalidValueException> {

    InvalidValueExceptionHandler(ErrorResponseProcessor<?> errorResponses) {
        super(errorResponses, HttpStatus.BAD_REQUEST);
    }
}
