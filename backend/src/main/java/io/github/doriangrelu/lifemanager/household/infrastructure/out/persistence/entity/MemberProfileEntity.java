package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.util.UUID;

/** Ligne de la table {@code household.member_profile} (annuaire local des noms). */
@MappedEntity("member_profile")
public record MemberProfileEntity(@Id UUID memberId, String displayName, Instant updatedAt) {
}
