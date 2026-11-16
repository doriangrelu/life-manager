package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.CreateDefaultCategories;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CreateDefaultCategoriesCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
/** Crée les catégories par défaut d'un nouveau foyer ; ne fait rien s'il en a déjà (événement reçu deux fois). */
@Singleton
class CreateDefaultCategoriesUseCase implements CreateDefaultCategories {

    private final CategoryRepository categories;

    CreateDefaultCategoriesUseCase(CategoryRepository categories) {
        this.categories = categories;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public void create(CreateDefaultCategoriesCommand command) {
        var household = new HouseholdId(command.householdId());
        if (!categories.existsForHousehold(household)) {
            Category.defaultsFor(household).forEach(categories::save);
        }
    }
}
