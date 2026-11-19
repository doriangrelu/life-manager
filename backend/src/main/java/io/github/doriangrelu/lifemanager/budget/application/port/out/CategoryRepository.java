package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;

import java.util.List;
import java.util.Optional;

/** Stockage des catégories (de dépenses ou de rentrées : un nom n'est unique que pour un même sens). */
public interface CategoryRepository {

    Optional<Category> findById(CategoryId id);

    /** Catégories du foyer pour ce sens, triées par nom. */
    List<Category> findAllByHousehold(HouseholdId householdId, CategoryKind kind);

    boolean existsForHousehold(HouseholdId householdId, CategoryKind kind);

    /** Vrai si une autre catégorie que {@code ignored}, même foyer et même sens, porte déjà ce nom (sans majuscules). */
    boolean isNameTaken(HouseholdId householdId, CategoryKind kind, CategoryName name, CategoryId ignored);

    /** Catégorie du foyer de ce sens portant ce nom, sans tenir compte des majuscules. */
    Optional<Category> findByName(HouseholdId householdId, CategoryKind kind, CategoryName name);

    /**
     * Crée une nouvelle catégorie si son nom est encore libre dans le foyer pour son sens.
     *
     * @return {@code false} si une autre catégorie a pris ce nom entre-temps (rien n'est créé)
     */
    boolean addIfNameFree(Category category);

    /** Crée la catégorie ou enregistre ses modifications. */
    void save(Category category);
}
