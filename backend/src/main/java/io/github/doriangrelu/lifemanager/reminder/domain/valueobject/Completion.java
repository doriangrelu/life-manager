package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Ce qui a été fait : titre recopié du rappel (il peut être supprimé ensuite), date prévue,
 * moment où c'est fait et membre qui l'a fait.
 */
public record Completion(ReminderTitle title, LocalDate dueOn, Instant doneAt, MemberId doneBy) {

    public Completion {
        Require.notNull(title, "Le titre du rappel fait");
        Require.notNull(dueOn, "La date prévue du rappel fait");
        Require.notNull(doneAt, "Le moment où le rappel est fait");
        Require.notNull(doneBy, "Le membre qui a fait le rappel");
    }
}
