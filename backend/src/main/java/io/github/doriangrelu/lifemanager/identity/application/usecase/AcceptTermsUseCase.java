package io.github.doriangrelu.lifemanager.identity.application.usecase;

import io.github.doriangrelu.lifemanager.identity.application.port.in.AcceptTerms;
import io.github.doriangrelu.lifemanager.identity.application.port.in.command.AcceptTermsCommand;
import io.github.doriangrelu.lifemanager.identity.application.port.out.UserAccountRepository;
import io.github.doriangrelu.lifemanager.identity.application.service.TermsPolicy;
import io.github.doriangrelu.lifemanager.identity.domain.exception.UserAccountNotFoundException;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Enregistre l'acceptation des conditions en vigueur par l'utilisateur connecté. */
@Singleton
class AcceptTermsUseCase implements AcceptTerms {

    private final UserAccountRepository accounts;
    private final TermsPolicy terms;
    private final Clock clock;

    AcceptTermsUseCase(UserAccountRepository accounts, TermsPolicy terms, Clock clock) {
        this.accounts = accounts;
        this.terms = terms;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "identity")
    public void accept(AcceptTermsCommand command) {
        var id = UserId.of(command.subject());
        var account = accounts.findById(id).orElseThrow(() -> new UserAccountNotFoundException(id));
        account.acceptTerms(terms.currentVersion(), command.version(), clock.instant());
        accounts.save(account);
    }
}
