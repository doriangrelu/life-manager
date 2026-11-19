package io.github.doriangrelu.lifemanager.identity.application.port.out;

import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;

/** Comptes de connexion chez Keycloak. */
public interface LoginAccounts {

    /**
     * Supprime le compte de connexion (déjà absent : rien à faire).
     *
     * @throws io.github.doriangrelu.lifemanager.kernel.domain.exception.UpstreamFailureException si Keycloak refuse
     */
    void delete(UserId id);
}
