package io.github.doriangrelu.lifemanager.kernel.domain.exception;

/**
 * Erreur métier : une règle du domaine empêche l'opération.
 * Le message est destiné à être lu par un humain.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
