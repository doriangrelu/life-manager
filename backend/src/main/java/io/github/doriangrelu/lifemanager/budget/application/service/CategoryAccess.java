package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.exception.CategoryNotFoundException;
import io.github.doriangrelu.lifemanager.budget.domain.exception.DuplicateCategoryNameException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Code commun aux use cases qui modifient une catégorie : la retrouver, vérifier l'unicité de son nom. */
@Singleton
public class CategoryAccess {

    private final BudgetAccess access;
    private final CategoryRepository categories;

    CategoryAccess(BudgetAccess access, CategoryRepository categories) {
        this.access = access;
        this.categories = categories;
    }

    /**
     * Charge une catégorie du foyer, pour un membre de ce foyer.
     *
     * @throws CategoryNotFoundException si la catégorie n'existe pas dans ce foyer
     */
    public Category loadForMember(UUID householdId, UUID categoryId, String requesterId) {
        var household = access.householdOfMember(householdId, requesterId);
        return categories.findById(new CategoryId(categoryId))
                .filter(category -> category.belongsTo(household))
                .orElseThrow(CategoryNotFoundException::new);
    }

    /** @throws DuplicateCategoryNameException si une autre catégorie du foyer porte déjà ce nom */
    public void ensureNameIsFree(HouseholdId householdId, CategoryName name, CategoryId ignored) {
        if (categories.isNameTaken(householdId, name, ignored)) {
            throw new DuplicateCategoryNameException();
        }
    }
}
