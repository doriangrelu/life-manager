package io.github.doriangrelu.lifemanager.identity.application.service;

import io.github.doriangrelu.lifemanager.identity.application.port.out.UserAccountRepository;
import io.github.doriangrelu.lifemanager.identity.domain.exception.UserAccountNotFoundException;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/**
 * Efface le profil local dans sa propre transaction ; l'événement {@code UserDeletedV1} part avec
 * (boîte d'envoi) pour que les foyers soient quittés.
 */
@Singleton
public class AccountEraser {

    private final UserAccountRepository accounts;
    private final DomainEventPublisher events;
    private final Clock clock;

    AccountEraser(UserAccountRepository accounts, DomainEventPublisher events, Clock clock) {
        this.accounts = accounts;
        this.events = events;
        this.clock = clock;
    }

    /** @throws io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException si l'e-mail diffère */
    @Transactional(transactionManager = "identity")
    public void erase(UserId id, String confirmEmail) {
        var account = accounts.findById(id).orElseThrow(() -> new UserAccountNotFoundException(id));
        account.delete(confirmEmail, clock.instant());
        accounts.delete(id);
        events.publishEventsOf(account);
    }
}
