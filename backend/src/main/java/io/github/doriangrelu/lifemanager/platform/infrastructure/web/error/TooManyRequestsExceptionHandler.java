package io.github.doriangrelu.lifemanager.platform.infrastructure.web.error;

import io.github.doriangrelu.lifemanager.platform.infrastructure.web.exception.TooManyRequestsException;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

/** Trop de tentatives → 429 Too Many Requests. */
@Singleton
@Produces
class TooManyRequestsExceptionHandler extends StatusExceptionHandler<TooManyRequestsException> {

    TooManyRequestsExceptionHandler(ErrorResponseProcessor<?> errorResponses) {
        super(errorResponses, HttpStatus.TOO_MANY_REQUESTS);
    }
}
