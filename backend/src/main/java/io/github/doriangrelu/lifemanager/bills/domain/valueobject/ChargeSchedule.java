package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Calendrier d'une charge : première échéance et fréquence.
 * Chaque échéance est calculée depuis la première (jamais de proche en proche) : une charge du 31
 * tombe le 28 février puis de nouveau le 31 mars.
 * Une dernière échéance facultative borne le calendrier (fin d'un crédit) : rien n'est prévu après elle.
 */
public record ChargeSchedule(LocalDate firstDueOn, Frequency frequency, LocalDate lastDueOn) {

    public ChargeSchedule {
        Require.notNull(firstDueOn, "La première échéance");
        Require.notNull(frequency, "La fréquence");
        if (lastDueOn != null && lastDueOn.isBefore(firstDueOn)) {
            throw new InvalidValueException("La dernière échéance ne peut pas précéder la première");
        }
    }

    /** Calendrier sans fin. */
    public ChargeSchedule(LocalDate firstDueOn, Frequency frequency) {
        this(firstDueOn, frequency, null);
    }

    /** Vrai si ce jour tombe après la dernière échéance (jamais pour un calendrier sans fin). */
    public boolean endsBefore(LocalDate day) {
        return lastDueOn != null && day.isAfter(lastDueOn);
    }

    /** Date de l'échéance numéro {@code index} (0 = la première). */
    public LocalDate dueOn(int index) {
        return firstDueOn.plusMonths((long) frequency.months() * index);
    }

    /** Dates d'échéance comprises entre deux jours inclus, dans l'ordre (au plus 100 ans parcourus). */
    public List<LocalDate> dueDatesBetween(LocalDate from, LocalDate to) {
        var dates = new ArrayList<LocalDate>();
        var end = endsBefore(to) ? lastDueOn : to;
        for (int index = 0; index < 1200 && !dueOn(index).isAfter(end); index++) {
            if (!dueOn(index).isBefore(from)) {
                dates.add(dueOn(index));
            }
        }
        return dates;
    }
}
