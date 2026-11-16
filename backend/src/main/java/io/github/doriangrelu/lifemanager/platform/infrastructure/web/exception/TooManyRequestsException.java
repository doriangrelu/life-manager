package io.github.doriangrelu.lifemanager.platform.infrastructure.web.exception;

/** L'utilisateur a fait trop de tentatives : il doit patienter (réponse 429). */
public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
        super(message);
    }
}
