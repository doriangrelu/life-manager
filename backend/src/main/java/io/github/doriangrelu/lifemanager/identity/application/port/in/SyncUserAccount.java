package io.github.doriangrelu.lifemanager.identity.application.port.in;

import io.github.doriangrelu.lifemanager.identity.application.port.in.command.SyncUserAccountCommand;

/**
 * Crée ou met à jour le profil local d'un utilisateur à partir de ce que Keycloak vient de confirmer.
 */
public interface SyncUserAccount {

    void sync(SyncUserAccountCommand command);
}
