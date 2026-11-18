package io.github.doriangrelu.lifemanager.household.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.repository.HouseholdOutboxJdbcRepository;
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

/** Boîte d'envoi du domaine household : sa table, ses transactions. */
@Factory
class HouseholdOutboxFactory {

    @Singleton
    @Named("household")
    Outbox householdOutbox(HouseholdOutboxJdbcRepository table, @Named("household") TransactionOperations<Connection> transactions,
                    OutboxCodec codec, ApplicationEventPublisher<Object> eventBus, Clock clock) {
        return new Outbox(new OutboxStorage("household", table, transactions), codec, eventBus, clock);
    }
}
