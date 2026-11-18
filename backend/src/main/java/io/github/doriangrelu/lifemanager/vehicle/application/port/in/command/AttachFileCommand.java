package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;

/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param vehicleId   véhicule concerné
 * @param entryId     opération du carnet
 * @param fileName    nom d'origine envoyé par le navigateur
 * @param content     contenu du fichier (10 Mo au plus) ; simple transport, jamais comparé
 */
public record AttachFileCommand(String requesterId, UUID householdId, UUID vehicleId, UUID entryId, String fileName,
                                byte[] content) {
}
