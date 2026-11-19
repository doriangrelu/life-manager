package io.github.doriangrelu.lifemanager.budget.application.port.in.query;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.time.YearMonth;
import java.util.UUID;

/**
 * Demande du bilan d'une période de mois entiers.
 * La période n'est vérifiée qu'après le contrôle d'accès au foyer : un non-membre reçoit toujours 404.
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param from        premier mois inclus
 * @param to          dernier mois inclus, au plus {@value #MAX_MONTHS} mois après {@code from}
 */
public record GetBudgetReportQuery(String requesterId, UUID householdId, YearMonth from, YearMonth to) {

    public static final int MAX_MONTHS = 24;

    /** @throws InvalidValueException si la période est absente, à l'envers ou trop longue */
    public void ensureValidPeriod() {
        if (from == null || to == null || to.isBefore(from) || from.plusMonths(MAX_MONTHS - 1L).isBefore(to)) {
            throw new InvalidValueException("La période doit couvrir de 1 à " + MAX_MONTHS + " mois");
        }
    }
}
