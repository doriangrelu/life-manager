package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    /** Dates d'échéance comprises entre deux jours inclus, dans l'ordre (au plus 100 ans parcourus). */
    public List<LocalDate> dueDatesBetween(LocalDate from, LocalDate to) {
        var dates = new ArrayList<LocalDate>();
        for (int index = 0; index < 1200 && !dueOn(index).isAfter(to); index++) {
            if (!dueOn(index).isBefore(from)) {
                dates.add(dueOn(index));
            }
        }
        return dates;
    }
}
