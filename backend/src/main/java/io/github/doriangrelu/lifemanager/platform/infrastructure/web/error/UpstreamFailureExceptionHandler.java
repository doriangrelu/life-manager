package io.github.doriangrelu.lifemanager.platform.infrastructure.web.error;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.UpstreamFailureException;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

/** Service extérieur en échec → 502 Bad Gateway, avec le message pour l'utilisateur. */
@Singleton
@Produces
class UpstreamFailureExceptionHandler extends StatusExceptionHandler<UpstreamFailureException> {

    UpstreamFailureExceptionHandler(ErrorResponseProcessor<?> errorResponses) {
        super(errorResponses, HttpStatus.BAD_GATEWAY);
    }
}
