package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.util.UUID;

/** Ligne de la table {@code shopping.purchase_history}. */
@MappedEntity("purchase_history")
public record PurchaseHistoryEntity(@Id UUID id, UUID listId, String label, Instant lastPurchasedAt,
                                    int purchaseCount) {
}
