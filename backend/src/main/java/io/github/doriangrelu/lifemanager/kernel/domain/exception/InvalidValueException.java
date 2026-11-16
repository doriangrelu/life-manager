package io.github.doriangrelu.lifemanager.kernel.domain.exception;

/**
 * Donnée de forme invalide (vide, trop longue, mauvais format…).
 * Levée à la construction d'un value object pour qu'aucun objet métier invalide n'existe.
 */
public class InvalidValueException extends DomainException {

    public InvalidValueException(String message) {
        super(message);
    }
}
