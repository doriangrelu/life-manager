package io.github.doriangrelu.lifemanager.identity.application.usecase;

import io.github.doriangrelu.lifemanager.identity.application.port.in.GetCurrentUser;
import io.github.doriangrelu.lifemanager.identity.application.port.in.GetCurrentUserQuery;
import io.github.doriangrelu.lifemanager.identity.application.port.in.UserProfile;
import io.github.doriangrelu.lifemanager.identity.application.port.out.UserAccountRepository;
import io.github.doriangrelu.lifemanager.identity.domain.exception.UserAccountNotFoundException;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Lit le profil de l'utilisateur authentifié. */
@Singleton
class GetCurrentUserUseCase implements GetCurrentUser {

    private final UserAccountRepository accounts;

    GetCurrentUserUseCase(UserAccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    @Transactional(transactionManager = "identity", readOnly = true)
    public UserProfile get(GetCurrentUserQuery query) {
        var id = UserId.of(query.subject());
        var account = accounts.findById(id).orElseThrow(() -> new UserAccountNotFoundException(id));
        return new UserProfile(id.value(), account.email().value(), account.displayName().value());
    }
}
