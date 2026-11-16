package io.github.doriangrelu.lifemanager.identity.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.identity.application.port.in.SyncUserAccount;
import io.github.doriangrelu.lifemanager.identity.application.port.in.SyncUserAccountCommand;
import io.github.doriangrelu.lifemanager.platform.infrastructure.security.event.UserAuthenticated;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/**
 * Tient le profil local à jour chaque fois que Keycloak confirme l'identité d'un utilisateur.
 * Écoute synchrone : le profil existe avant que la requête n'atteigne un contrôleur.
 */
@Singleton
class UserAuthenticatedListener {

    private final SyncUserAccount syncUserAccount;

    UserAuthenticatedListener(SyncUserAccount syncUserAccount) {
        this.syncUserAccount = syncUserAccount;
    }

    @EventListener
    void on(UserAuthenticated event) {
        syncUserAccount.sync(new SyncUserAccountCommand(event.subject(), event.email(), event.displayName()));
    }
}
