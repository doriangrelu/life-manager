package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;

/**
 * Calendrier d'une charge : première échéance et fréquence.
 * Chaque échéance est calculée depuis la première (jamais de proche en proche) : une charge du 31
 * tombe le 28 février puis de nouveau le 31 mars.
 */
public record ChargeSchedule(LocalDate firstDueOn, Frequency frequency) {

    public ChargeSchedule {
        Require.notNull(firstDueOn, "La première échéance");
        Require.notNull(frequency, "La fréquence");
    }

    /** Date de l'échéance numéro {@code index} (0 = la première). */
    public LocalDate dueOn(int index) {
        return firstDueOn.plusMonths((long) frequency.months() * index);
    }
}
