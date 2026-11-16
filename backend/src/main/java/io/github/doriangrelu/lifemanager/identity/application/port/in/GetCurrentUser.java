package io.github.doriangrelu.lifemanager.identity.application.port.in;

/**
 * Donne le profil de l'utilisateur à l'origine de la requête (« whoami »).
 */
public interface GetCurrentUser {

    UserProfile get(GetCurrentUserQuery query);
}
