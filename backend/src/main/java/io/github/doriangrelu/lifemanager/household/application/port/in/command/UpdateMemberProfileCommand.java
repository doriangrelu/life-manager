package io.github.doriangrelu.lifemanager.household.application.port.in.command;

import java.util.UUID;
/**
 * @param memberId    {@code sub} Keycloak du membre
 * @param displayName son nouveau nom d'affichage
 */
public record UpdateMemberProfileCommand(UUID memberId, String displayName) {
}
