package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ReorderShoppingItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ReorderShoppingItemsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.exception.ShoppingItemNotFoundException;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.stream.Collectors;

/** Range les articles d'une sous-liste ; un article inconnu ou d'une autre sous-liste fait tout refuser. */
@Singleton
class ReorderShoppingItemsUseCase implements ReorderShoppingItems {

    private final ShoppingListAccess access;
    private final ShoppingItemRepository items;

    ReorderShoppingItemsUseCase(ShoppingListAccess access, ShoppingItemRepository items) {
        this.access = access;
        this.items = items;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void reorder(ReorderShoppingItemsCommand command) {
        var list = access.listForMember(command.householdId(), command.listId(), command.requesterId());
        var known = items.findAllByList(list.id()).stream().map(ShoppingItem::id).collect(Collectors.toSet());
        var ordered = command.itemIds().stream().distinct().map(ShoppingItemId::new).toList();
        if (!known.containsAll(ordered)) {
            throw new ShoppingItemNotFoundException();
        }
        items.reorder(list.id(), ordered);
    }
}
