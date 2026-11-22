package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.ListProjectExpenses;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.ProjectExpensesQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ExpenseView;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ProjectExpenses;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ExpenseRepository;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ProjectRefs;
import io.github.doriangrelu.lifemanager.budget.application.service.BudgetAccess;
import io.github.doriangrelu.lifemanager.budget.domain.exception.ProjectNotFoundException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.Set;

/** Dépenses d'un projet et leur total, pour sa fiche. */
@Singleton
class ListProjectExpensesUseCase implements ListProjectExpenses {

    private final BudgetAccess access;
    private final ProjectRefs projects;
    private final ExpenseRepository expenses;

    ListProjectExpensesUseCase(BudgetAccess access, ProjectRefs projects, ExpenseRepository expenses) {
        this.access = access;
        this.projects = projects;
        this.expenses = expenses;
    }

    @Override
    @Transactional(transactionManager = "budget", readOnly = true)
    public ProjectExpenses list(ProjectExpensesQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        var project = projects.find(household, new ProjectId(query.projectId()))
                .orElseThrow(ProjectNotFoundException::new);
        var offBudget = project.countsInBudget() ? Set.<ProjectId>of() : Set.of(project.id());
        var views = expenses.findForProject(household, project.id()).stream()
                .map(expense -> ExpenseView.from(expense, offBudget))
                .toList();
        return new ProjectExpenses(views.stream().mapToLong(ExpenseView::amountCents).sum(), views);
    }
}
