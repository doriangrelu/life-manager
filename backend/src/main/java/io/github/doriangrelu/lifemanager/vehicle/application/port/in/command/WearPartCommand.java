package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;

/**
 * Action sur les pièces d'usure d'un véhicule du foyer.
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param partId      pièce visée, ou {@code null} pour une pose
 * @param input       saisie, ou {@code null} pour une suppression
 */
public record WearPartCommand(String requesterId, UUID householdId, UUID vehicleId, UUID partId,
                              WearPartInput input) {
}
