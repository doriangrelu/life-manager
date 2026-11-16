package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.util.UUID;

/**
 * Ligne de la table {@code shopping.shopping_item}.
 *
 * @param checkedAt moment du cochage, ou {@code null} si l'article n'est pas coché
 */
@MappedEntity("shopping_item")
public record ShoppingItemEntity(@Id UUID id, UUID householdId, String label, @Nullable Instant checkedAt,
                                 UUID addedBy, Instant addedAt) {
}
