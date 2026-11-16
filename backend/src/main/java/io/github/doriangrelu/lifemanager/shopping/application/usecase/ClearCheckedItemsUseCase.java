package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ClearCheckedItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ClearCheckedItemsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Fin des courses : retire d'un coup tous les articles cochés (aucune règle d'article en jeu, une seule requête). */
@Singleton
class ClearCheckedItemsUseCase implements ClearCheckedItems {

    private final ShoppingAccess access;
    private final ShoppingItemRepository items;

    ClearCheckedItemsUseCase(ShoppingAccess access, ShoppingItemRepository items) {
        this.access = access;
        this.items = items;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void clear(ClearCheckedItemsCommand command) {
        items.deleteChecked(access.householdOfMember(command.householdId(), command.requesterId()));
    }
}
