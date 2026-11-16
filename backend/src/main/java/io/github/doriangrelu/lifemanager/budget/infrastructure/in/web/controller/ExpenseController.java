package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.budget.application.port.in.CorrectExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.DeleteExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CorrectExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.DeleteExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.ExpenseRequest;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.ExpenseResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.UUID;

/** Dépenses d'un foyer : saisie, correction, suppression. */
@Controller("/api/households/{householdId}/expenses")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ExpenseController {

    private final RecordExpense recordExpense;
    private final CorrectExpense correctExpense;
    private final DeleteExpense deleteExpense;

    ExpenseController(RecordExpense recordExpense, CorrectExpense correctExpense, DeleteExpense deleteExpense) {
        this.recordExpense = recordExpense;
        this.correctExpense = correctExpense;
        this.deleteExpense = deleteExpense;
    }

    @Post
    HttpResponse<ExpenseResponse> record(Authentication user, UUID householdId,
                                         @Body @Valid ExpenseRequest request) {
        var command = new RecordExpenseCommand(user.getName(), householdId, request.toInput());
        return HttpResponse.created(ExpenseResponse.from(recordExpense.record(command)));
    }

    @Put("/{expenseId}")
    ExpenseResponse correct(Authentication user, UUID householdId, UUID expenseId,
                            @Body @Valid ExpenseRequest request) {
        var command = new CorrectExpenseCommand(user.getName(), householdId, expenseId, request.toInput());
        return ExpenseResponse.from(correctExpense.correct(command));
    }

    @Delete("/{expenseId}")
    HttpResponse<Void> delete(Authentication user, UUID householdId, UUID expenseId) {
        deleteExpense.delete(new DeleteExpenseCommand(user.getName(), householdId, expenseId));
        return HttpResponse.noContent();
    }
}
