package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.household.domain.aggregate.Invitation;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationCode;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationStatus;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity.InvitationEntity;

/** Conversion entre l'agrégat {@link Invitation} et sa ligne en base. */
public final class InvitationMapper {

    private InvitationMapper() {
    }

    public static InvitationEntity toEntity(Invitation invitation) {
        return new InvitationEntity(
                invitation.id().value(),
                invitation.householdId().value(),
                invitation.code().value(),
                invitation.status().name(),
                invitation.expiresAt(),
                invitation.acceptedBy().map(MemberId::value).orElse(null));
    }

    public static Invitation toDomain(InvitationEntity entity) {
        return Invitation.restore(
                new InvitationId(entity.id()),
                new HouseholdId(entity.householdId()),
                new InvitationCode(entity.code()),
                entity.expiresAt(),
                InvitationStatus.valueOf(entity.status()),
                entity.acceptedBy() == null ? null : new MemberId(entity.acceptedBy()));
    }
}
