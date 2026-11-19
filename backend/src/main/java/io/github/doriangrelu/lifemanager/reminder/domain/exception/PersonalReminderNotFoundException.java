package io.github.doriangrelu.lifemanager.reminder.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Rappel inconnu, ou d'un autre foyer (même erreur dans les deux cas). */
public class PersonalReminderNotFoundException extends NotFoundException {

    public PersonalReminderNotFoundException() {
        super("Rappel introuvable");
    }
}
