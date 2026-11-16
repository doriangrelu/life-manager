package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.AddCategory;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.AddCategoryCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.BudgetAccess;
import io.github.doriangrelu.lifemanager.budget.application.service.CategoryAccess;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
/** Ajoute une catégorie à un foyer, avec un nom pas encore utilisé dans ce foyer. */
@Singleton
class AddCategoryUseCase implements AddCategory {

    private final BudgetAccess access;
    private final CategoryAccess categoryAccess;
    private final CategoryRepository categories;

    AddCategoryUseCase(BudgetAccess access, CategoryAccess categoryAccess, CategoryRepository categories) {
        this.access = access;
        this.categoryAccess = categoryAccess;
        this.categories = categories;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public CategoryView add(AddCategoryCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var category = Category.create(CategoryId.newId(), household, new CategoryName(command.name()));
        categoryAccess.ensureNameIsFree(household, category.name(), category.id());
        categories.save(category);
        return CategoryView.from(category);
    }
}
