package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ListShoppingLists;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.query.ListShoppingListsQuery;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingListView;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingListRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Donne les sous-listes du foyer, avec leur nombre d'articles à acheter. */
@Singleton
class ListShoppingListsUseCase implements ListShoppingLists {

    private final ShoppingListAccess access;
    private final ShoppingListRepository lists;
    private final ShoppingItemRepository items;

    ListShoppingListsUseCase(ShoppingListAccess access, ShoppingListRepository lists, ShoppingItemRepository items) {
        this.access = access;
        this.lists = lists;
        this.items = items;
    }

    @Override
    @Transactional(transactionManager = "shopping", readOnly = true)
    public List<ShoppingListView> list(ListShoppingListsQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        var pending = items.countUncheckedByList(household);
        return lists.findAllByHousehold(household).stream()
                .map(list -> ShoppingListView.from(list, pending.getOrDefault(list.id(), 0L)))
                .toList();
    }
}
