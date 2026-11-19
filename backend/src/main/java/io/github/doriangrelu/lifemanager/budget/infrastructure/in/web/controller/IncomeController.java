package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.budget.application.port.in.CorrectIncome;
import io.github.doriangrelu.lifemanager.budget.application.port.in.DeleteIncome;
import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordIncome;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CorrectIncomeCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.DeleteIncomeCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordIncomeCommand;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.IncomeRequest;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.IncomeResponse;
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

/** Rentrées d'argent d'un foyer : saisie, correction, suppression. */
@Controller("/api/households/{householdId}/incomes")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class IncomeController {

    private final RecordIncome recordIncome;
    private final CorrectIncome correctIncome;
    private final DeleteIncome deleteIncome;

    IncomeController(RecordIncome recordIncome, CorrectIncome correctIncome, DeleteIncome deleteIncome) {
        this.recordIncome = recordIncome;
        this.correctIncome = correctIncome;
        this.deleteIncome = deleteIncome;
    }

    @Post
    HttpResponse<IncomeResponse> record(Authentication user, UUID householdId, @Body @Valid IncomeRequest request) {
        var command = new RecordIncomeCommand(user.getName(), householdId, request.toInput());
        return HttpResponse.created(IncomeResponse.from(recordIncome.record(command)));
    }

    @Put("/{incomeId}")
    IncomeResponse correct(Authentication user, UUID householdId, UUID incomeId, @Body @Valid IncomeRequest request) {
        var command = new CorrectIncomeCommand(user.getName(), householdId, incomeId, request.toInput());
        return IncomeResponse.from(correctIncome.correct(command));
    }

    @Delete("/{incomeId}")
    HttpResponse<Void> delete(Authentication user, UUID householdId, UUID incomeId) {
        deleteIncome.delete(new DeleteIncomeCommand(user.getName(), householdId, incomeId));
        return HttpResponse.noContent();
    }
}
