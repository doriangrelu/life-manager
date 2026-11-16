package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.RenameCategory;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RenameCategoryCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.CategoryAccess;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
/** Renomme une catégorie d'un foyer, avec un nom pas encore utilisé dans ce foyer. */
@Singleton
class RenameCategoryUseCase implements RenameCategory {

    private final CategoryAccess categoryAccess;
    private final CategoryRepository categories;

    RenameCategoryUseCase(CategoryAccess categoryAccess, CategoryRepository categories) {
        this.categoryAccess = categoryAccess;
        this.categories = categories;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public CategoryView rename(RenameCategoryCommand command) {
        var category = categoryAccess.loadForMember(command.householdId(), command.categoryId(), command.requesterId());
        var newName = new CategoryName(command.name());
        categoryAccess.ensureNameIsFree(category.householdId(), newName, category.id());
        category.rename(newName);
        categories.save(category);
        return CategoryView.from(category);
    }
}
