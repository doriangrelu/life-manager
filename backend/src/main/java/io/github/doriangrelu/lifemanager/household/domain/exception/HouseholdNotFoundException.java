package io.github.doriangrelu.lifemanager.household.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/**
 * Foyer inexistant, ou dont l'utilisateur n'est pas membre :
 * les deux cas donnent la même erreur pour ne pas révéler l'existence du foyer.
 */
public class HouseholdNotFoundException extends NotFoundException {

    public HouseholdNotFoundException() {
        super("Foyer introuvable");
    }
}
