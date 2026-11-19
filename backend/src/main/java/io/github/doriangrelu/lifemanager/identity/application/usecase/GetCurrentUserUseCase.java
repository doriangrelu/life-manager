package io.github.doriangrelu.lifemanager.identity.application.usecase;

import io.github.doriangrelu.lifemanager.identity.application.port.in.GetCurrentUser;
import io.github.doriangrelu.lifemanager.identity.application.port.in.query.GetCurrentUserQuery;
import io.github.doriangrelu.lifemanager.identity.application.port.in.result.UserProfile;
import io.github.doriangrelu.lifemanager.identity.application.port.out.UserAccountRepository;
import io.github.doriangrelu.lifemanager.identity.application.service.TermsPolicy;
import io.github.doriangrelu.lifemanager.identity.domain.exception.UserAccountNotFoundException;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Lit le profil de l'utilisateur authentifié, avec l'état de son acceptation des conditions. */
@Singleton
class GetCurrentUserUseCase implements GetCurrentUser {

    private final UserAccountRepository accounts;
    private final TermsPolicy terms;

    GetCurrentUserUseCase(UserAccountRepository accounts, TermsPolicy terms) {
        this.accounts = accounts;
        this.terms = terms;
    }

    @Override
    @Transactional(transactionManager = "identity", readOnly = true)
    public UserProfile get(GetCurrentUserQuery query) {
        var id = UserId.of(query.subject());
        var account = accounts.findById(id).orElseThrow(() -> new UserAccountNotFoundException(id));
        var version = terms.currentVersion();
        return new UserProfile(id.value(), account.email().value(), account.displayName().value(),
                account.hasAccepted(version), version);
    }
}
