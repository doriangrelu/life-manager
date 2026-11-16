package io.github.doriangrelu.lifemanager.household.application.port.in.result;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Détail d'un foyer pour l'un de ses membres.
 *
 * @param activeInvitation code d'invitation encore utilisable, s'il y en a un
 */
public record HouseholdDetails(UUID id, String name, List<MemberDetails> members,
                               Optional<InvitationDetails> activeInvitation) {
}
