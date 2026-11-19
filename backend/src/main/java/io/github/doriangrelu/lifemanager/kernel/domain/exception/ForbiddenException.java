package io.github.doriangrelu.lifemanager.kernel.domain.exception;

/**
 * Le demandeur a accès à la ressource, mais pas le droit de faire cette action
 * (ex. un membre qui n'est pas propriétaire du foyer).
 */
public abstract class ForbiddenException extends DomainException {

    protected ForbiddenException(String message) {
        super(message);
    }
}
