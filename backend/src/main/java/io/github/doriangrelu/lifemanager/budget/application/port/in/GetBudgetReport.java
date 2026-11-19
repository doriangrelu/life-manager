package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.query.GetBudgetReportQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.BudgetReport;

/** Bilan réalisé d'une période : rentrées, dépenses, résultat. */
public interface GetBudgetReport {

    BudgetReport get(GetBudgetReportQuery query);
}
