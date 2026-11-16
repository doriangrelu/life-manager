package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ListShoppingItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.query.ListShoppingItemsQuery;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;
/** Donne la liste de courses du foyer à l'un de ses membres. */
@Singleton
class ListShoppingItemsUseCase implements ListShoppingItems {

    private final ShoppingAccess access;
    private final ShoppingItemRepository items;

    ListShoppingItemsUseCase(ShoppingAccess access, ShoppingItemRepository items) {
        this.access = access;
        this.items = items;
    }

    @Override
    @Transactional(transactionManager = "shopping", readOnly = true)
    public List<ShoppingItemView> list(ListShoppingItemsQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        return items.findAllByHousehold(household).stream().map(ShoppingItemView::from).toList();
    }
}
