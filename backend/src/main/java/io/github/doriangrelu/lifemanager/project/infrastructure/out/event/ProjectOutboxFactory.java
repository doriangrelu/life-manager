package io.github.doriangrelu.lifemanager.project.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.repository.ProjectOutboxJdbcRepository;
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

/** Boîte d'envoi du domaine project : sa table, ses transactions. */
@Factory
class ProjectOutboxFactory {

    @Singleton
    @Named("project")
    Outbox projectOutbox(ProjectOutboxJdbcRepository table,
                         @Named("project") TransactionOperations<Connection> transactions,
                         OutboxCodec codec, ApplicationEventPublisher<Object> eventBus, Clock clock) {
        return new Outbox(new OutboxStorage("project", table, transactions), codec, eventBus, clock);
    }
}
