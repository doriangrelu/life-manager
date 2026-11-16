package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.budget.application.port.in.GetMonthlyBudget;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.GetMonthlyBudgetQuery;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.MonthlyBudgetResponse;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/** Vue mensuelle du budget d'un foyer ({@code GET /api/households/{id}/budget/2026-11}). */
@Controller("/api/households/{householdId}/budget")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class MonthlyBudgetController {

    private final GetMonthlyBudget getMonthlyBudget;

    MonthlyBudgetController(GetMonthlyBudget getMonthlyBudget) {
        this.getMonthlyBudget = getMonthlyBudget;
    }

    @Get("/{month}")
    MonthlyBudgetResponse month(Authentication user, UUID householdId, String month) {
        var query = new GetMonthlyBudgetQuery(user.getName(), householdId, parse(month));
        return MonthlyBudgetResponse.from(getMonthlyBudget.get(query));
    }

    private static YearMonth parse(String month) {
        try {
            return YearMonth.parse(month);
        } catch (DateTimeParseException invalid) {
            throw new InvalidValueException("Le mois doit être au format AAAA-MM");
        }
    }
}
