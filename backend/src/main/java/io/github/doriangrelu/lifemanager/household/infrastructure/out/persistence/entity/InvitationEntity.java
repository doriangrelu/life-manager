package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.util.UUID;

/** Ligne de la table {@code household.invitation}. */
@MappedEntity("invitation")
public record InvitationEntity(@Id UUID id, UUID householdId, String code, String status, Instant expiresAt,
                               @Nullable UUID acceptedBy) {
}
