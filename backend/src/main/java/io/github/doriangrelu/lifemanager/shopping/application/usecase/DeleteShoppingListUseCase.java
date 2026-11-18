package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.DeleteShoppingList;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingListCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingListRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Supprime une sous-liste du foyer, avec ses articles et son historique. */
@Singleton
class DeleteShoppingListUseCase implements DeleteShoppingList {

    private final ShoppingListAccess access;
    private final ShoppingListRepository lists;

    DeleteShoppingListUseCase(ShoppingListAccess access, ShoppingListRepository lists) {
        this.access = access;
        this.lists = lists;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void delete(ShoppingListCommand command) {
        lists.delete(access.listForMember(command.householdId(), command.listId(), command.requesterId()));
    }
}
