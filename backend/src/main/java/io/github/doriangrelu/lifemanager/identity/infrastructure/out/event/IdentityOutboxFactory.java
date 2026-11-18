package io.github.doriangrelu.lifemanager.identity.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.identity.infrastructure.out.persistence.repository.IdentityOutboxJdbcRepository;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.Outbox;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.OutboxCodec;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.OutboxStorage;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.transaction.TransactionOperations;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.sql.Connection;
import java.time.Clock;

/** Boîte d'envoi du domaine identity : sa table, ses transactions. */
@Factory
class IdentityOutboxFactory {

    @Singleton
    @Named("identity")
    Outbox identityOutbox(IdentityOutboxJdbcRepository table, @Named("identity") TransactionOperations<Connection> transactions,
                    OutboxCodec codec, ApplicationEventPublisher<Object> eventBus, Clock clock) {
        return new Outbox(new OutboxStorage("identity", table, transactions), codec, eventBus, clock);
    }
}
