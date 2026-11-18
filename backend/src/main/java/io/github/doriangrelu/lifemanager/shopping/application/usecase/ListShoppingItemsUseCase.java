package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ListShoppingItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.query.ListShoppingItemsQuery;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Donne les articles d'une sous-liste du foyer à l'un de ses membres. */
@Singleton
class ListShoppingItemsUseCase implements ListShoppingItems {

    private final ShoppingListAccess access;
    private final ShoppingItemRepository items;

    ListShoppingItemsUseCase(ShoppingListAccess access, ShoppingItemRepository items) {
        this.access = access;
        this.items = items;
    }

    @Override
    @Transactional(transactionManager = "shopping", readOnly = true)
    public List<ShoppingItemView> list(ListShoppingItemsQuery query) {
        var list = access.listForMember(query.householdId(), query.listId(), query.requesterId());
        return items.findAllByList(list.id()).stream().map(ShoppingItemView::from).toList();
    }
}
