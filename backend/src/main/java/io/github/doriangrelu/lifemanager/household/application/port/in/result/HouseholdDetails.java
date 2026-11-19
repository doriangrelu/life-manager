package io.github.doriangrelu.lifemanager.household.application.port.in.result;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Détail d'un foyer pour l'un de ses membres.
 *
 * @param ownerId          propriétaire du foyer
 * @param activeInvitation code d'invitation encore utilisable, {@code null} s'il n'y en a pas
 *                         ou si le demandeur n'est pas le propriétaire
 */
public record HouseholdDetails(UUID id, String name, UUID ownerId, List<MemberDetails> members,
                               InvitationDetails activeInvitation) {

    public Optional<InvitationDetails> optionalActiveInvitation() {
        return Optional.ofNullable(activeInvitation);
    }
}
