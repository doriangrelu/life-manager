package io.github.doriangrelu.lifemanager.kernel.domain.exception;

/**
 * L'objet demandé n'existe pas, ou l'utilisateur n'a pas le droit de savoir qu'il existe.
 */
public abstract class NotFoundException extends DomainException {

    protected NotFoundException(String message) {
        super(message);
    }
}
