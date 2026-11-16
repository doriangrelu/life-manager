package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;

import java.util.List;
import java.util.Optional;
/** Stockage des catégories de dépenses. */
public interface CategoryRepository {

    Optional<Category> findById(CategoryId id);

    /** Catégories du foyer, triées par nom. */
    List<Category> findAllByHousehold(HouseholdId householdId);

    boolean existsForHousehold(HouseholdId householdId);

    /** Vrai si une autre catégorie du foyer que {@code ignored} porte déjà ce nom (sans tenir compte des majuscules). */
    boolean isNameTaken(HouseholdId householdId, CategoryName name, CategoryId ignored);

    /** Crée la catégorie ou enregistre ses modifications. */
    void save(Category category);
}
