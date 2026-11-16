package io.github.doriangrelu.lifemanager.identity.application.usecase;

import io.github.doriangrelu.lifemanager.identity.application.port.in.SyncUserAccount;
import io.github.doriangrelu.lifemanager.identity.application.port.in.SyncUserAccountCommand;
import io.github.doriangrelu.lifemanager.identity.application.port.out.UserAccountRepository;
import io.github.doriangrelu.lifemanager.identity.domain.aggregate.UserAccount;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.DisplayName;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.EmailAddress;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.Instant;

/** Crée le profil au premier passage de l'utilisateur, puis le garde aligné sur Keycloak. */
@Singleton
class SyncUserAccountUseCase implements SyncUserAccount {

    private final UserAccountRepository accounts;
    private final DomainEventPublisher events;
    private final Clock clock;

    SyncUserAccountUseCase(UserAccountRepository accounts, DomainEventPublisher events, Clock clock) {
        this.accounts = accounts;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "identity")
    public void sync(SyncUserAccountCommand command) {
        var id = UserId.of(command.subject());
        var email = new EmailAddress(command.email());
        var displayName = new DisplayName(command.displayName());
        var now = clock.instant();

        var account = accounts.findById(id)
                .map(existing -> updated(existing, email, displayName, now))
                .orElseGet(() -> UserAccount.register(id, email, displayName, now));

        accounts.save(account);
        events.publishEventsOf(account);
    }

    private static UserAccount updated(UserAccount account, EmailAddress email, DisplayName name, Instant now) {
        account.updateProfile(email, name, now);
        return account;
    }
}
