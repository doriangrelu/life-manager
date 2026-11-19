package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.household.application.port.in.result.HouseholdDetails;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.UUID;

/**
 * Détail d'un foyer.
 *
 * @param ownerId          propriétaire du foyer (lui seul renomme, invite, retire, supprime)
 * @param activeInvitation code encore utilisable, {@code null} s'il n'y en a pas ou pour un non-propriétaire
 */
@Serdeable
public record HouseholdDetailsResponse(UUID id, String name, UUID ownerId, List<MemberResponse> members,
                                       @Nullable InvitationResponse activeInvitation) {

    public static HouseholdDetailsResponse from(HouseholdDetails details) {
        return new HouseholdDetailsResponse(details.id(), details.name(), details.ownerId(),
                details.members().stream().map(MemberResponse::from).toList(),
                details.optionalActiveInvitation().map(InvitationResponse::from).orElse(null));
    }
}
