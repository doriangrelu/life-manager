package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import java.time.YearMonth;

/** Rentrées, dépenses et résultat (rentrées moins dépenses, négatif possible) d'un mois, en centimes. */
public record MonthResult(YearMonth month, long incomeCents, long expenseCents) {

    public long resultCents() {
        return Math.subtractExact(incomeCents, expenseCents);
    }
}
