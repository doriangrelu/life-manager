package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;

/**
 * Quoi et quand : « contrôle technique de la Clio le 2027-03-01 ».
 *
 * @param kind    type d'échéance
 * @param subject ce qu'elle concerne
 * @param dueOn   date de l'échéance
 */
public record DeadlineDetails(DeadlineKind kind, Subject subject, LocalDate dueOn) {

    public DeadlineDetails {
        Require.notNull(kind, "Le type d'échéance");
        Require.notNull(subject, "Le sujet de l'échéance");
        Require.notNull(dueOn, "La date de l'échéance");
    }
}
