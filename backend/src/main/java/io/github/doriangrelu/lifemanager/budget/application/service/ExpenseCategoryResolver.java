package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.exception.UnusableCategoryException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import jakarta.inject.Singleton;

import java.util.UUID;

/**
 * Trouve la catégorie d'une dépense : une catégorie existante du foyer, ou un nom tapé librement
 * (catégorie réutilisée si le nom existe, réactivée si archivée, créée sinon).
 * À appeler dans la transaction de la dépense : si la dépense est refusée, rien n'est créé.
 */
@Singleton
public class ExpenseCategoryResolver {

    private final CategoryRepository categories;

    ExpenseCategoryResolver(CategoryRepository categories) {
        this.categories = categories;
    }

    /**
     * @throws InvalidValueException     si ni catégorie ni nom ne sont donnés, ou les deux
     * @throws UnusableCategoryException si la catégorie existante est archivée ou d'un autre foyer
     */
    public CategoryId resolve(HouseholdId householdId, UUID categoryId, String newCategoryName) {
        var hasId = categoryId != null;
        var hasName = newCategoryName != null && !newCategoryName.isBlank();
        if (hasId == hasName) { // les deux, ou aucun des deux
            throw new InvalidValueException("Indiquez une catégorie existante ou un nouveau nom de catégorie, pas les deux");
        }
        return hasName ? byName(householdId, new CategoryName(newCategoryName)) : existing(householdId, categoryId);
    }

    private CategoryId existing(HouseholdId householdId, UUID categoryId) {
        return categories.findById(new CategoryId(categoryId))
                .filter(category -> category.isUsableBy(householdId))
                .map(Category::id)
                .orElseThrow(UnusableCategoryException::new);
    }

    private CategoryId byName(HouseholdId householdId, CategoryName name) {
        var known = categories.findByName(householdId, name);
        if (known.isPresent()) {
            return reactivated(known.get());
        }
        var created = Category.create(CategoryId.newId(), householdId, name);
        if (categories.addIfNameFree(created)) {
            return created.id();
        }
        // Un autre membre vient de créer ce nom : on utilise sa catégorie.
        return categories.findByName(householdId, name).map(this::reactivated).orElseThrow(UnusableCategoryException::new);
    }

    private CategoryId reactivated(Category category) {
        if (category.archived()) {
            category.reactivate();
            categories.save(category);
        }
        return category.id();
    }
}
