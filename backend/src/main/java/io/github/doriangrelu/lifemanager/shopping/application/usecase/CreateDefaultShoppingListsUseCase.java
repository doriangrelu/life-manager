package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.CreateDefaultShoppingLists;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.CreateDefaultShoppingListsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingListRepository;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingList;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Crée les sous-listes par défaut d'un nouveau foyer ; ne fait rien s'il en a déjà (événement reçu deux fois). */
@Singleton
class CreateDefaultShoppingListsUseCase implements CreateDefaultShoppingLists {

    private final ShoppingListRepository lists;

    CreateDefaultShoppingListsUseCase(ShoppingListRepository lists) {
        this.lists = lists;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void create(CreateDefaultShoppingListsCommand command) {
        var household = new HouseholdId(command.householdId());
        if (!lists.existsForHousehold(household)) {
            ShoppingList.defaultsFor(household).forEach(lists::save);
        }
    }
}
