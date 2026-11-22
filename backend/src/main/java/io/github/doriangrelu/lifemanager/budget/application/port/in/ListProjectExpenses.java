package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.query.ProjectExpensesQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ProjectExpenses;

/** Dépenses rattachées à un projet du foyer. */
public interface ListProjectExpenses {

    ProjectExpenses list(ProjectExpensesQuery query);
}
