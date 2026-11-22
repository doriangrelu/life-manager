package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ProjectExpenses;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

/** Dépenses d'un projet et leur total en centimes. */
@Serdeable
public record ProjectExpensesResponse(long totalCents, List<ExpenseResponse> expenses) {

    public static ProjectExpensesResponse from(ProjectExpenses result) {
        return new ProjectExpensesResponse(result.totalCents(),
                result.expenses().stream().map(ExpenseResponse::from).toList());
    }
}
