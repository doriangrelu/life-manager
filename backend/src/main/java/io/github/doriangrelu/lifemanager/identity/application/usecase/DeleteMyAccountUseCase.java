package io.github.doriangrelu.lifemanager.identity.application.usecase;

import io.github.doriangrelu.lifemanager.identity.application.port.in.DeleteMyAccount;
import io.github.doriangrelu.lifemanager.identity.application.port.in.command.DeleteMyAccountCommand;
import io.github.doriangrelu.lifemanager.identity.application.port.out.LoginAccounts;
import io.github.doriangrelu.lifemanager.identity.application.service.AccountEraser;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import jakarta.inject.Singleton;

/**
 * Supprime le compte : d'abord les données (profil effacé, foyers quittés par événement), puis le compte
 * de connexion Keycloak. Si Keycloak échoue, les données sont déjà parties : une nouvelle connexion
 * recrée un profil vide, et l'utilisateur peut recommencer.
 */
@Singleton
class DeleteMyAccountUseCase implements DeleteMyAccount {

    private final AccountEraser eraser;
    private final LoginAccounts logins;

    DeleteMyAccountUseCase(AccountEraser eraser, LoginAccounts logins) {
        this.eraser = eraser;
        this.logins = logins;
    }

    @Override
    public void delete(DeleteMyAccountCommand command) {
        var id = UserId.of(command.subject());
        eraser.erase(id, command.confirmEmail());
        logins.delete(id);
    }
}
