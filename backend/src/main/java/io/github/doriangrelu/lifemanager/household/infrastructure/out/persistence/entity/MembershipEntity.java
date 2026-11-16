package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.util.UUID;

/**
 * Ligne de la table {@code household.membership}.
 * L'identifiant technique {@code id} n'existe que pour la base ; le couple (foyer, membre) est unique.
 */
@MappedEntity("membership")
public record MembershipEntity(@Id UUID id, UUID householdId, UUID memberId, Instant joinedAt) {
}
