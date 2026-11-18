package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingList;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ListName;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.entity.ShoppingListEntity;

/** Conversion entre l'agrégat {@link ShoppingList} et sa ligne en base. */
public final class ShoppingListMapper {

    private ShoppingListMapper() {
    }

    public static ShoppingListEntity toEntity(ShoppingList list) {
        return new ShoppingListEntity(list.id().value(), list.householdId().value(), list.name().value());
    }

    public static ShoppingList toDomain(ShoppingListEntity entity) {
        return ShoppingList.restore(new ShoppingListId(entity.id()), new HouseholdId(entity.householdId()),
                new ListName(entity.name()));
    }
}
