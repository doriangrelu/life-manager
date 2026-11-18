package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.RenameShoppingList;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.RenameShoppingListCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingListView;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingListRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListNames;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Renomme une sous-liste du foyer. */
@Singleton
class RenameShoppingListUseCase implements RenameShoppingList {

    private final ShoppingListAccess access;
    private final ShoppingListNames names;
    private final ShoppingListRepository lists;
    private final ShoppingItemRepository items;

    RenameShoppingListUseCase(ShoppingListAccess access, ShoppingListNames names, ShoppingListRepository lists,
                              ShoppingItemRepository items) {
        this.access = access;
        this.names = names;
        this.lists = lists;
        this.items = items;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public ShoppingListView rename(RenameShoppingListCommand command) {
        var list = access.listForMember(command.householdId(), command.listId(), command.requesterId());
        list.rename(names.freeNameFor(list, command.name()));
        lists.save(list);
        long pending = items.findAllByList(list.id()).stream().filter(item -> !item.isChecked()).count();
        return ShoppingListView.from(list, pending);
    }
}
