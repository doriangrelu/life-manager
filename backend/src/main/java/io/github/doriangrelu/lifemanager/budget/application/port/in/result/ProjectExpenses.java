package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import java.util.List;

/** Dépenses d'un projet (toutes dates) et leur total en centimes. */
public record ProjectExpenses(long totalCents, List<ExpenseView> expenses) {
}
