package io.github.doriangrelu.lifemanager.bills.application.port.in.query;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Échéances encore attendues d'une période (jours inclus).
 * La période n'est vérifiée qu'après le contrôle d'accès au foyer : un non-membre reçoit toujours 404.
 */
public record ForecastQuery(String requesterId, UUID householdId, LocalDate from, LocalDate to) {

    /** Au plus deux ans : de quoi couvrir les « 12 derniers mois » comme une année entière. */
    public static final int MAX_DAYS = 731;

    /** @throws InvalidValueException si la période est absente, à l'envers ou trop longue */
    public void ensureValidPeriod() {
        if (from == null || to == null || to.isBefore(from) || from.plusDays(MAX_DAYS).isBefore(to)) {
            throw new InvalidValueException("La période doit couvrir de 1 à " + MAX_DAYS + " jours");
        }
    }
}
