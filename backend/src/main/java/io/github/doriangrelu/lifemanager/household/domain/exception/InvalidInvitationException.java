package io.github.doriangrelu.lifemanager.household.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/**
 * Code d'invitation inconnu, expiré ou déjà utilisé.
 * Un seul message pour les trois cas : on ne donne pas d'indice à qui essaie de deviner des codes.
 */
public class InvalidInvitationException extends InvalidValueException {

    public InvalidInvitationException() {
        super("Ce code d'invitation n'est pas valide");
    }
}
