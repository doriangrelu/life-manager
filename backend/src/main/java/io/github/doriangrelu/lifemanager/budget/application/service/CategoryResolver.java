package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.exception.UnusableCategoryException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import jakarta.inject.Singleton;

import java.util.UUID;

/**
 * Trouve la catégorie d'une dépense ou d'une rentrée : une catégorie existante du foyer et du bon sens,
 * ou un nom tapé librement (catégorie réutilisée si le nom existe, réactivée si archivée, créée sinon).
 * À appeler dans la transaction de la saisie : si elle est refusée, rien n'est créé.
 */
@Singleton
public class CategoryResolver {

    private final CategoryRepository categories;

    CategoryResolver(CategoryRepository categories) {
        this.categories = categories;
    }

    /**
     * @throws InvalidValueException     si ni catégorie ni nom ne sont donnés, ou les deux
     * @throws UnusableCategoryException si la catégorie existante est archivée, d'un autre foyer ou de l'autre sens
     */
    public CategoryId resolve(HouseholdId householdId, CategoryKind kind, UUID categoryId, String newCategoryName) {
        var hasId = categoryId != null;
        var hasName = newCategoryName != null && !newCategoryName.isBlank();
        if (hasId == hasName) { // les deux, ou aucun des deux
            throw new InvalidValueException("Indiquez une catégorie existante ou un nouveau nom de catégorie, pas les deux");
        }
        return hasName
                ? byName(householdId, kind, new CategoryName(newCategoryName))
                : existing(householdId, kind, categoryId);
    }

    private CategoryId existing(HouseholdId householdId, CategoryKind kind, UUID categoryId) {
        return categories.findById(new CategoryId(categoryId))
                .filter(category -> category.isUsableFor(householdId, kind))
                .map(Category::id)
                .orElseThrow(UnusableCategoryException::new);
    }

    private CategoryId byName(HouseholdId householdId, CategoryKind kind, CategoryName name) {
        var known = categories.findByName(householdId, kind, name);
        if (known.isPresent()) {
            return reactivated(known.get());
        }
        var created = Category.create(CategoryId.newId(), householdId, name, kind);
        if (categories.addIfNameFree(created)) {
            return created.id();
        }
        // Un autre membre vient de créer ce nom : on utilise sa catégorie.
        return categories.findByName(householdId, kind, name).map(this::reactivated)
                .orElseThrow(UnusableCategoryException::new);
    }

    private CategoryId reactivated(Category category) {
        if (category.archived()) {
            category.reactivate();
            categories.save(category);
        }
        return category.id();
    }
}
