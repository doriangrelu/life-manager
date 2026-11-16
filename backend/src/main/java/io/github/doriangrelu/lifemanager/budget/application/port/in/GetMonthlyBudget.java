package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.query.GetMonthlyBudgetQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthlyBudget;
/** Synthèse et détail des dépenses d'un foyer pour un mois. */
public interface GetMonthlyBudget {

    MonthlyBudget get(GetMonthlyBudgetQuery query);
}
