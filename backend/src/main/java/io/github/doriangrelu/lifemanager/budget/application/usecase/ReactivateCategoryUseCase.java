package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.ReactivateCategory;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CategoryActionCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.CategoryAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
/** Réactive une catégorie archivée. */
@Singleton
class ReactivateCategoryUseCase implements ReactivateCategory {

    private final CategoryAccess categoryAccess;
    private final CategoryRepository categories;

    ReactivateCategoryUseCase(CategoryAccess categoryAccess, CategoryRepository categories) {
        this.categoryAccess = categoryAccess;
        this.categories = categories;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public CategoryView reactivate(CategoryActionCommand command) {
        var category = categoryAccess.loadForMember(command.householdId(), command.kind(), command.categoryId(),
                command.requesterId());
        category.reactivate();
        categories.save(category);
        return CategoryView.from(category);
    }
}
