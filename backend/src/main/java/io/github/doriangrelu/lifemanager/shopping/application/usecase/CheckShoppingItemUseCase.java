package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.CheckShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingItemCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Coche un article d'une sous-liste du foyer. */
@Singleton
class CheckShoppingItemUseCase implements CheckShoppingItem {

    private final ShoppingListAccess access;
    private final ShoppingItemRepository items;
    private final Clock clock;

    CheckShoppingItemUseCase(ShoppingListAccess access, ShoppingItemRepository items, Clock clock) {
        this.access = access;
        this.items = items;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public ShoppingItemView check(ShoppingItemCommand command) {
        var item = access.itemForMember(command.householdId(), command.listId(), command.itemId(),
                command.requesterId());
        item.check(clock);
        items.save(item);
        return ShoppingItemView.from(item);
    }
}
