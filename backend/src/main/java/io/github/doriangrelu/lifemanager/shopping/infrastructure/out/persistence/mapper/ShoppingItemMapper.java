package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemLabel;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemOrigin;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.entity.ShoppingItemEntity;

/** Conversion entre l'agrégat {@link ShoppingItem} et sa ligne en base. */
public final class ShoppingItemMapper {

    private ShoppingItemMapper() {
    }

    public static ShoppingItemEntity toEntity(ShoppingItem item) {
        return new ShoppingItemEntity(item.id().value(), item.listId().value(), item.label().value(),
                item.checkedAt().orElse(null), item.origin().addedBy().value(), item.origin().addedAt());
    }

    public static ShoppingItem toDomain(ShoppingItemEntity entity) {
        var origin = new ItemOrigin(new MemberId(entity.addedBy()), entity.addedAt());
        return ShoppingItem.restore(new ShoppingItemId(entity.id()), new ShoppingListId(entity.listId()),
                new ItemLabel(entity.label()), origin, entity.checkedAt());
    }
}
