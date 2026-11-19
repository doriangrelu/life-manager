package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.ListCategories;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.ListCategoriesQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.BudgetAccess;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/**
 * Liste les catégories d'un foyer, de dépenses ou de rentrées, pour l'un de ses membres.
 * Les catégories de rentrées suggérées sont créées à la première demande (le suivi des rentrées est optionnel).
 */
@Singleton
class ListCategoriesUseCase implements ListCategories {

    private final BudgetAccess access;
    private final CategoryRepository categories;

    ListCategoriesUseCase(BudgetAccess access, CategoryRepository categories) {
        this.access = access;
        this.categories = categories;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public List<CategoryView> list(ListCategoriesQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        if (query.kind() == CategoryKind.INCOME) {
            addIncomeDefaultsOnce(household);
        }
        return categories.findAllByHousehold(household, query.kind()).stream().map(CategoryView::from).toList();
    }

    private void addIncomeDefaultsOnce(HouseholdId household) {
        if (!categories.existsForHousehold(household, CategoryKind.INCOME)) {
            // addIfNameFree : deux premiers appels simultanés ne créent pas de doublon
            Category.defaultsFor(household, CategoryKind.INCOME).forEach(categories::addIfNameFree);
        }
    }
}
