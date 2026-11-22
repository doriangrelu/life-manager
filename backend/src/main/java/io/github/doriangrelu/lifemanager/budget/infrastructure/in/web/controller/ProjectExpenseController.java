package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.budget.application.port.in.ListProjectExpenses;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.ProjectExpensesQuery;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.ProjectExpensesResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.util.UUID;

/** Dépenses rattachées à un projet (le projet lui-même est servi par le domaine project). */
@Controller("/api/households/{householdId}/projects/{projectId}/expenses")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ProjectExpenseController {

    private final ListProjectExpenses listExpenses;

    ProjectExpenseController(ListProjectExpenses listExpenses) {
        this.listExpenses = listExpenses;
    }

    @Get
    ProjectExpensesResponse list(Authentication user, UUID householdId, UUID projectId) {
        return ProjectExpensesResponse.from(
                listExpenses.list(new ProjectExpensesQuery(user.getName(), householdId, projectId)));
    }
}
