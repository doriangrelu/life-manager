package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ReorderShoppingLists;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ReorderShoppingListsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingListRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingList;
import io.github.doriangrelu.lifemanager.shopping.domain.exception.ShoppingListNotFoundException;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.stream.Collectors;

/** Range les sous-listes du foyer ; une sous-liste inconnue ou d'un autre foyer fait tout refuser. */
@Singleton
class ReorderShoppingListsUseCase implements ReorderShoppingLists {

    private final ShoppingListAccess access;
    private final ShoppingListRepository lists;

    ReorderShoppingListsUseCase(ShoppingListAccess access, ShoppingListRepository lists) {
        this.access = access;
        this.lists = lists;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void reorder(ReorderShoppingListsCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var known = lists.findAllByHousehold(household).stream().map(ShoppingList::id).collect(Collectors.toSet());
        var ordered = command.listIds().stream().distinct().map(ShoppingListId::new).toList();
        if (!known.containsAll(ordered)) {
            throw new ShoppingListNotFoundException();
        }
        lists.reorder(household, ordered);
    }
}
