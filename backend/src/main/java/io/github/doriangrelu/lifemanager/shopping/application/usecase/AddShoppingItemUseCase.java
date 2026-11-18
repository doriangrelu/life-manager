package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.AddShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.AddShoppingItemCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemLabel;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Ajoute un article à une sous-liste du foyer d'un membre. */
@Singleton
class AddShoppingItemUseCase implements AddShoppingItem {

    private final ShoppingListAccess access;
    private final ShoppingItemRepository items;
    private final Clock clock;

    AddShoppingItemUseCase(ShoppingListAccess access, ShoppingItemRepository items, Clock clock) {
        this.access = access;
        this.items = items;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public ShoppingItemView add(AddShoppingItemCommand command) {
        var list = access.listForMember(command.householdId(), command.listId(), command.requesterId());
        var item = ShoppingItem.add(ShoppingItemId.newId(), list.id(), new ItemLabel(command.label()),
                MemberId.of(command.requesterId()), clock);
        items.save(item);
        return ShoppingItemView.from(item);
    }
}
