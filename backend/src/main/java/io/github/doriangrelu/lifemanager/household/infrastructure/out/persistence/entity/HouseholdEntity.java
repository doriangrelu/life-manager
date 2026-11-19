package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.util.UUID;

/**
 * Ligne de la table {@code household.household}.
 *
 * @param incomeTracking réglage « suivre les rentrées (bilan) »
 */
@MappedEntity("household")
public record HouseholdEntity(@Id UUID id, String name, Instant createdAt, UUID ownerId, boolean incomeTracking) {
}
