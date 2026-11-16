package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.ListCategories;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.ListCategoriesQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.BudgetAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;
/** Liste les catégories d'un foyer pour l'un de ses membres. */
@Singleton
class ListCategoriesUseCase implements ListCategories {

    private final BudgetAccess access;
    private final CategoryRepository categories;

    ListCategoriesUseCase(BudgetAccess access, CategoryRepository categories) {
        this.access = access;
        this.categories = categories;
    }

    @Override
    @Transactional(transactionManager = "budget", readOnly = true)
    public List<CategoryView> list(ListCategoriesQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        return categories.findAllByHousehold(household).stream().map(CategoryView::from).toList();
    }
}
