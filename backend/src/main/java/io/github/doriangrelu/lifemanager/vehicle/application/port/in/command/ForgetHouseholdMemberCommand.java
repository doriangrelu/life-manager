package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;

/**
 * Un membre a quitté le foyer (ou en a été retiré).
 *
 * @param ownerId propriétaire du foyer à ce moment-là
 */
public record ForgetHouseholdMemberCommand(UUID householdId, UUID memberId, UUID ownerId) {
}
