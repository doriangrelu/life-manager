package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.Authorship;
import jakarta.inject.Named;
import java.util.UUID;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.AddShoppingList;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.AddShoppingListCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingListView;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingListRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListNames;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingList;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Ajoute une sous-liste vide au foyer d'un membre. */
@Singleton
class AddShoppingListUseCase implements AddShoppingList {

    private final ShoppingListAccess access;
    private final ShoppingListNames names;
    private final ShoppingListRepository lists;
    private final Authorship authorship;

    AddShoppingListUseCase(ShoppingListAccess access, ShoppingListNames names, ShoppingListRepository lists,
                           @Named("shopping") Authorship authorship) {
        this.authorship = authorship;
        this.access = access;
        this.names = names;
        this.lists = lists;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public ShoppingListView add(AddShoppingListCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var list = ShoppingList.create(ShoppingListId.newId(), household, names.freeName(household, command.name()));
        lists.save(list);
        authorship.record(list.id().value(), household.value(), UUID.fromString(command.requesterId()));
        return ShoppingListView.from(list, 0);
    }
}
