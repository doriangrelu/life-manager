package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.ArchiveCategory;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CategoryActionCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.CategoryAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
/** Archive une catégorie : elle n'est plus proposée pour les nouvelles dépenses. */
@Singleton
class ArchiveCategoryUseCase implements ArchiveCategory {

    private final CategoryAccess categoryAccess;
    private final CategoryRepository categories;

    ArchiveCategoryUseCase(CategoryAccess categoryAccess, CategoryRepository categories) {
        this.categoryAccess = categoryAccess;
        this.categories = categories;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public CategoryView archive(CategoryActionCommand command) {
        var category = categoryAccess.loadForMember(command.householdId(), command.categoryId(), command.requesterId());
        category.archive();
        categories.save(category);
        return CategoryView.from(category);
    }
}
