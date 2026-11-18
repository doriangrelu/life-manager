package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.util.UUID;

/** Ligne de la table {@code bills.household_member} (copie locale des membres de foyer). */
@MappedEntity("household_member")
public record HouseholdMemberEntity(@Id UUID id, UUID householdId, UUID memberId) {
}
