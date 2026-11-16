package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.util.UUID;

/** Ligne de la table {@code budget.category}. */
@MappedEntity("category")
public record CategoryEntity(@Id UUID id, UUID householdId, String name, boolean archived) {
}
