package io.github.doriangrelu.lifemanager.identity.application.port.in;

import io.github.doriangrelu.lifemanager.identity.application.port.in.query.GetCurrentUserQuery;
import io.github.doriangrelu.lifemanager.identity.application.port.in.result.UserProfile;

/**
 * Donne le profil de l'utilisateur à l'origine de la requête (« whoami »).
 */
public interface GetCurrentUser {

    UserProfile get(GetCurrentUserQuery query);
}
