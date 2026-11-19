package io.github.doriangrelu.lifemanager.reminder.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Ligne d'historique inconnue, ou d'un autre foyer (même erreur dans les deux cas). */
public class ReminderDoneNotFoundException extends NotFoundException {

    public ReminderDoneNotFoundException() {
        super("Ligne d'historique introuvable");
    }
}
