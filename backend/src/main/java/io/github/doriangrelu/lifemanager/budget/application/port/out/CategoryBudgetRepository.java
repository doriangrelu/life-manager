package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.CategoryBudget;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.BudgetScope;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;

import java.time.YearMonth;
import java.util.List;

/** Stockage des règles de budget prévu : une seule par catégorie, mois et portée. */
public interface CategoryBudgetRepository {

    /** Enregistre la règle ; elle remplace celle de même catégorie, même mois et même portée. */
    void save(CategoryBudget rule);

    /** Supprime la règle de cette catégorie pour ce mois et cette portée, si elle existe. */
    void delete(CategoryId categoryId, YearMonth month, BudgetScope scope);

    /** Toutes les règles du foyer datées de ce mois ou d'avant. */
    List<CategoryBudget> findAllUntil(HouseholdId householdId, YearMonth month);
}
