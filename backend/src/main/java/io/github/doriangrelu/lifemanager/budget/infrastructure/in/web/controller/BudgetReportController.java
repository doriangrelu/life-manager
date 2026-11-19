package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.budget.application.port.in.GetBudgetReport;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.GetBudgetReportQuery;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.BudgetReportResponse;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/** Bilan d'une période ({@code GET /api/households/{id}/report?from=2026-10&to=2026-12}). */
@Controller("/api/households/{householdId}/report")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class BudgetReportController {

    private final GetBudgetReport getBudgetReport;

    BudgetReportController(GetBudgetReport getBudgetReport) {
        this.getBudgetReport = getBudgetReport;
    }

    @Get
    BudgetReportResponse report(Authentication user, UUID householdId, @Nullable @QueryValue String from,
                                @Nullable @QueryValue String to) {
        var query = new GetBudgetReportQuery(user.getName(), householdId, parse(from), parse(to));
        return BudgetReportResponse.from(getBudgetReport.get(query));
    }

    // Mois absent : null, refusé par le use case après le contrôle d'accès.
    private static YearMonth parse(String month) {
        try {
            return month == null ? null : YearMonth.parse(month);
        } catch (DateTimeParseException invalid) {
            throw new InvalidValueException("Le mois doit être au format AAAA-MM");
        }
    }
}
