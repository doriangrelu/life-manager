package io.github.doriangrelu.lifemanager.kernel.domain.exception;

/**
 * L'opération est valide en soi mais incompatible avec l'état actuel
 * (ex. déjà membre du foyer, plafond atteint).
 */
public abstract class ConflictException extends DomainException {

    protected ConflictException(String message) {
        super(message);
    }
}
