package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ClearCheckedItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ClearCheckedItemsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Fin des courses : retire d'un coup tous les articles cochés d'une sous-liste. */
@Singleton
class ClearCheckedItemsUseCase implements ClearCheckedItems {

    private final ShoppingListAccess access;
    private final ShoppingItemRepository items;

    ClearCheckedItemsUseCase(ShoppingListAccess access, ShoppingItemRepository items) {
        this.access = access;
        this.items = items;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void clear(ClearCheckedItemsCommand command) {
        var list = access.listForMember(command.householdId(), command.listId(), command.requesterId());
        items.deleteChecked(list.id());
    }
}
