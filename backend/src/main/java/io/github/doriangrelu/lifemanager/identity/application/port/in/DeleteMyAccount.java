package io.github.doriangrelu.lifemanager.identity.application.port.in;

import io.github.doriangrelu.lifemanager.identity.application.port.in.command.DeleteMyAccountCommand;

/** Suppression du compte par l'utilisateur : profil effacé, foyers quittés, compte Keycloak supprimé. */
public interface DeleteMyAccount {

    void delete(DeleteMyAccountCommand command);
}
