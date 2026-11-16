package io.github.doriangrelu.lifemanager.identity.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.util.UUID;

/** Ligne de la table {@code identity.user_account}. */
@MappedEntity("user_account")
public record UserAccountEntity(@Id UUID id, String email, String displayName, Instant registeredAt) {
}
