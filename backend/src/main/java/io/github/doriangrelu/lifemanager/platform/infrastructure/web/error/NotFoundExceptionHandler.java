package io.github.doriangrelu.lifemanager.platform.infrastructure.web.error;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

/** Objet introuvable (ou invisible pour l'utilisateur) → 404 Not Found. */
@Singleton
@Produces
class NotFoundExceptionHandler extends StatusExceptionHandler<NotFoundException> {

    NotFoundExceptionHandler(ErrorResponseProcessor<?> errorResponses) {
        super(errorResponses, HttpStatus.NOT_FOUND);
    }
}
