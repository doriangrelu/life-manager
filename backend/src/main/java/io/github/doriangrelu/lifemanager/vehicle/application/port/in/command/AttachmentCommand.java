package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;

/**
 * Geste sur une pièce jointe existante (télécharger, supprimer).
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param vehicleId   véhicule concerné
 * @param entryId     opération du carnet
 * @param attachmentId pièce jointe visée
 */
public record AttachmentCommand(String requesterId, UUID householdId, UUID vehicleId, UUID entryId,
                                UUID attachmentId) {
}
