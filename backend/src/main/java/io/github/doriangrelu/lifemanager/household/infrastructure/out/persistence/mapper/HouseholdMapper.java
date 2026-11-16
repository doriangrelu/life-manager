package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.entity.Membership;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdName;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity.HouseholdEntity;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity.MembershipEntity;

import java.util.List;
import java.util.UUID;

/** Conversion entre l'agrégat {@link Household} et ses lignes en base (foyer + appartenances). */
public final class HouseholdMapper {

    private HouseholdMapper() {
    }

    public static HouseholdEntity toEntity(Household household) {
        return new HouseholdEntity(household.id().value(), household.name().value(), household.createdAt());
    }

    /** Nouvelle ligne d'appartenance (l'identifiant technique est tiré au hasard). */
    public static MembershipEntity toNewEntity(HouseholdId householdId, Membership membership) {
        return new MembershipEntity(UUID.randomUUID(), householdId.value(),
                membership.memberId().value(), membership.joinedAt());
    }

    public static Household toDomain(HouseholdEntity household, List<MembershipEntity> memberships) {
        var members = memberships.stream()
                .map(row -> new Membership(new MemberId(row.memberId()), row.joinedAt()))
                .toList();
        return Household.restore(new HouseholdId(household.id()), new HouseholdName(household.name()),
                household.createdAt(), members);
    }
}
