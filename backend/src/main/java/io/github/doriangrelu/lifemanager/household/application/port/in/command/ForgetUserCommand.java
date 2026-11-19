package io.github.doriangrelu.lifemanager.household.application.port.in.command;

import java.util.UUID;

/** @param userId identifiant ({@code sub} Keycloak) de l'utilisateur qui a supprimé son compte */
public record ForgetUserCommand(UUID userId) {
}
