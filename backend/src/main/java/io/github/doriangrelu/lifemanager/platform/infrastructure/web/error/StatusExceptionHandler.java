package io.github.doriangrelu.lifemanager.platform.infrastructure.web.error;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;

/**
 * Transforme une exception en réponse HTTP avec un statut donné et le message de l'exception,
 * au format d'erreur standard de Micronaut (le même pour toute l'API).
 *
 * @param <E> type d'exception traité
 */
abstract class StatusExceptionHandler<E extends Throwable> implements ExceptionHandler<E, HttpResponse<?>> {

    private final ErrorResponseProcessor<?> errorResponses;
    private final HttpStatus status;

    protected StatusExceptionHandler(ErrorResponseProcessor<?> errorResponses, HttpStatus status) {
        this.errorResponses = errorResponses;
        this.status = status;
    }

    @Override
    public HttpResponse<?> handle(HttpRequest request, E exception) {
        var context = ErrorContext.builder(request)
                .cause(exception)
                .errorMessage(exception.getMessage())
                .build();
        return errorResponses.processResponse(context, HttpResponse.status(status));
    }
}
