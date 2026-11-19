package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.util.UUID;

/**
 * Ligne de la table {@code shopping.shopping_list}.
 *
 * @param position rang dans le foyer (ordre choisi par le foyer), géré par le stockage
 */
@MappedEntity("shopping_list")
public record ShoppingListEntity(@Id UUID id, UUID householdId, String name, int position) {
}
