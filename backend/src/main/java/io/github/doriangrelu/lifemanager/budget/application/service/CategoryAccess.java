package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.exception.CategoryNotFoundException;
import io.github.doriangrelu.lifemanager.budget.domain.exception.DuplicateCategoryNameException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
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
     * Charge une catégorie du foyer et du sens attendu, pour un membre de ce foyer.
     *
     * @throws CategoryNotFoundException si la catégorie n'existe pas dans ce foyer pour ce sens
     */
    public Category loadForMember(UUID householdId, CategoryKind kind, UUID categoryId, String requesterId) {
        var household = access.householdOfMember(householdId, requesterId);
        return categories.findById(new CategoryId(categoryId))
                .filter(category -> category.belongsTo(household) && category.kind() == kind)
                .orElseThrow(CategoryNotFoundException::new);
    }

    /** @throws DuplicateCategoryNameException si une autre catégorie du foyer, de ce sens, porte déjà ce nom */
    public void ensureNameIsFree(HouseholdId householdId, CategoryKind kind, CategoryName name, CategoryId ignored) {
        if (categories.isNameTaken(householdId, kind, name, ignored)) {
            throw new DuplicateCategoryNameException();
        }
    }
}
