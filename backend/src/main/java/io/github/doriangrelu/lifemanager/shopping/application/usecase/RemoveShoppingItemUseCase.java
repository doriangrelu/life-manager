package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.RemoveShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingItemCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Supprime un article de la liste du foyer. */
@Singleton
class RemoveShoppingItemUseCase implements RemoveShoppingItem {

    private final ShoppingAccess access;
    private final ShoppingItemRepository items;

    RemoveShoppingItemUseCase(ShoppingAccess access, ShoppingItemRepository items) {
        this.access = access;
        this.items = items;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void remove(ShoppingItemCommand command) {
        items.delete(access.loadForMember(command.householdId(), command.itemId(), command.requesterId()));
    }
}
