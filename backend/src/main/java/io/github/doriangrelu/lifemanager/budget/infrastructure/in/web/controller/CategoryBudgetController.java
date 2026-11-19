package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.budget.application.port.in.SetCategoryBudget;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.SetCategoryBudgetCommand;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.CategoryBudgetRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.UUID;

/** Budget prévu d'une catégorie de dépenses : le fixer ou le retirer, à partir d'un mois ou pour un seul mois. */
@Controller("/api/households/{householdId}/expense-categories/{categoryId}/budget")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class CategoryBudgetController {

    private final SetCategoryBudget setCategoryBudget;

    CategoryBudgetController(SetCategoryBudget setCategoryBudget) {
        this.setCategoryBudget = setCategoryBudget;
    }

    @Put
    HttpResponse<Void> set(Authentication user, UUID householdId, UUID categoryId,
                           @Body @Valid CategoryBudgetRequest request) {
        setCategoryBudget.set(new SetCategoryBudgetCommand(user.getName(), householdId, categoryId,
                request.toMonth(), request.scope(), request.plannedCents()));
        return HttpResponse.noContent();
    }
}
