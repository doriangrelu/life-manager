package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.PurchaseHistoryEntry;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemLabel;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.PurchaseHistoryEntryId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.entity.PurchaseHistoryEntity;

/** Conversion entre l'agrégat {@link PurchaseHistoryEntry} et sa ligne en base. */
public final class PurchaseHistoryMapper {

    private PurchaseHistoryMapper() {
    }

    public static PurchaseHistoryEntity toEntity(PurchaseHistoryEntry entry) {
        return new PurchaseHistoryEntity(entry.id().value(), entry.listId().value(), entry.label().value(),
                entry.lastPurchasedAt(), entry.purchaseCount());
    }

    public static PurchaseHistoryEntry toDomain(PurchaseHistoryEntity entity) {
        return PurchaseHistoryEntry.restore(new PurchaseHistoryEntryId(entity.id()),
                new ShoppingListId(entity.listId()), new ItemLabel(entity.label()), entity.lastPurchasedAt(),
                entity.purchaseCount());
    }
}
