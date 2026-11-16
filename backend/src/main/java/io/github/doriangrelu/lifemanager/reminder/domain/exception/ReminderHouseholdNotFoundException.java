package io.github.doriangrelu.lifemanager.reminder.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Foyer inconnu du domaine rappels, ou dont l'utilisateur n'est pas membre (même erreur dans les deux cas). */
public class ReminderHouseholdNotFoundException extends NotFoundException {

    public ReminderHouseholdNotFoundException() {
        super("Foyer introuvable");
    }
}
