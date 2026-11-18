package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.RemoveShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingItemCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Supprime un article d'une sous-liste, sans l'inscrire à l'historique (il n'a pas été acheté). */
@Singleton
class RemoveShoppingItemUseCase implements RemoveShoppingItem {

    private final ShoppingListAccess access;
    private final ShoppingItemRepository items;

    RemoveShoppingItemUseCase(ShoppingListAccess access, ShoppingItemRepository items) {
        this.access = access;
        this.items = items;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void remove(ShoppingItemCommand command) {
        items.delete(access.itemForMember(command.householdId(), command.listId(), command.itemId(),
                command.requesterId()));
    }
}
