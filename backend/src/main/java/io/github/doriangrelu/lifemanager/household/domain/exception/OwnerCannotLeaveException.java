package io.github.doriangrelu.lifemanager.household.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;

/** Le propriétaire ne peut pas quitter son foyer (ni s'en retirer) : il le transmet ou le supprime d'abord. */
public class OwnerCannotLeaveException extends ConflictException {

    public OwnerCannotLeaveException() {
        super("Le propriétaire doit d'abord transmettre la propriété du foyer, ou le supprimer");
    }
}
