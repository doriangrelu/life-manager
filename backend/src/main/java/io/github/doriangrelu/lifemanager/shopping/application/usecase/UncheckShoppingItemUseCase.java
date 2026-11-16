package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.UncheckShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingItemCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Décoche un article de la liste du foyer. */
@Singleton
class UncheckShoppingItemUseCase implements UncheckShoppingItem {

    private final ShoppingAccess access;
    private final ShoppingItemRepository items;

    UncheckShoppingItemUseCase(ShoppingAccess access, ShoppingItemRepository items) {
        this.access = access;
        this.items = items;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public ShoppingItemView uncheck(ShoppingItemCommand command) {
        var item = access.loadForMember(command.householdId(), command.itemId(), command.requesterId());
        item.uncheck();
        items.save(item);
        return ShoppingItemView.from(item);
    }
}
