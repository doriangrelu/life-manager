package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.budget.application.port.in.AddCategory;
import io.github.doriangrelu.lifemanager.budget.application.port.in.ArchiveCategory;
import io.github.doriangrelu.lifemanager.budget.application.port.in.ListCategories;
import io.github.doriangrelu.lifemanager.budget.application.port.in.ReactivateCategory;
import io.github.doriangrelu.lifemanager.budget.application.port.in.RenameCategory;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.AddCategoryCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CategoryActionCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RenameCategoryCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.ListCategoriesQuery;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.CategoryRequest;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.CategoryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

/** Catégories de dépenses d'un foyer : liste, ajout, renommage, archivage, réactivation. */
@Controller("/api/households/{householdId}/expense-categories")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ExpenseCategoryController {

    private final ListCategories listCategories;
    private final AddCategory addCategory;
    private final RenameCategory renameCategory;
    private final ArchiveCategory archiveCategory;
    private final ReactivateCategory reactivateCategory;

    ExpenseCategoryController(ListCategories listCategories, AddCategory addCategory, RenameCategory renameCategory,
                              ArchiveCategory archiveCategory, ReactivateCategory reactivateCategory) {
        this.listCategories = listCategories;
        this.addCategory = addCategory;
        this.renameCategory = renameCategory;
        this.archiveCategory = archiveCategory;
        this.reactivateCategory = reactivateCategory;
    }

    @Get
    List<CategoryResponse> list(Authentication user, UUID householdId) {
        return listCategories.list(new ListCategoriesQuery(user.getName(), householdId)).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Post
    HttpResponse<CategoryResponse> add(Authentication user, UUID householdId, @Body @Valid CategoryRequest request) {
        var added = addCategory.add(new AddCategoryCommand(user.getName(), householdId, request.name()));
        return HttpResponse.created(CategoryResponse.from(added));
    }

    @Put("/{categoryId}")
    CategoryResponse rename(Authentication user, UUID householdId, UUID categoryId,
                            @Body @Valid CategoryRequest request) {
        var command = new RenameCategoryCommand(user.getName(), householdId, categoryId, request.name());
        return CategoryResponse.from(renameCategory.rename(command));
    }

    @Post("/{categoryId}/archive")
    CategoryResponse archive(Authentication user, UUID householdId, UUID categoryId) {
        var command = new CategoryActionCommand(user.getName(), householdId, categoryId);
        return CategoryResponse.from(archiveCategory.archive(command));
    }

    @Post("/{categoryId}/reactivate")
    CategoryResponse reactivate(Authentication user, UUID householdId, UUID categoryId) {
        var command = new CategoryActionCommand(user.getName(), householdId, categoryId);
        return CategoryResponse.from(reactivateCategory.reactivate(command));
    }
}
