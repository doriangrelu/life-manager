package io.github.doriangrelu.lifemanager.bills.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.repository.BillsOutboxJdbcRepository;
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

/** Boîte d'envoi du domaine bills : sa table, ses transactions. */
@Factory
class BillsOutboxFactory {

    @Singleton
    @Named("bills")
    Outbox billsOutbox(BillsOutboxJdbcRepository table, @Named("bills") TransactionOperations<Connection> transactions,
                    OutboxCodec codec, ApplicationEventPublisher<Object> eventBus, Clock clock) {
        return new Outbox(new OutboxStorage("bills", table, transactions), codec, eventBus, clock);
    }
}
