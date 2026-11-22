package io.github.doriangrelu.lifemanager.project.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.project.contract.event.ProjectChangedV1;
import io.github.doriangrelu.lifemanager.project.contract.event.ProjectDeletedV1;
import io.github.doriangrelu.lifemanager.project.contract.event.TaskDeadlineChangedV1;
import io.github.doriangrelu.lifemanager.project.domain.event.ProjectChanged;
import io.github.doriangrelu.lifemanager.project.domain.event.ProjectDeleted;
import io.github.doriangrelu.lifemanager.project.domain.event.TaskDeadlineChanged;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.Outbox;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * Traduit les événements internes des projets en événements de contrat pour les autres domaines.
 * Ils sont écrits dans la boîte d'envoi avec le changement (même transaction), puis distribués.
 */
@Singleton
class ProjectContractEventRelay {

    private final Outbox outbox;

    ProjectContractEventRelay(@Named("project") Outbox outbox) {
        this.outbox = outbox;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "project")
    void on(ProjectChanged event) {
        var details = event.details();
        outbox.record(new ProjectChangedV1(event.projectId().value(), event.householdId().value(),
                details.name().value(), details.color().name(), details.status().name(), details.countsInBudget(),
                details.plannedCents(), event.occurredAt()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "project")
    void on(ProjectDeleted event) {
        outbox.record(new ProjectDeletedV1(event.projectId().value(), event.householdId().value(),
                event.occurredAt()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "project")
    void on(TaskDeadlineChanged event) {
        outbox.record(new TaskDeadlineChangedV1(event.taskId().value(), event.householdId().value(),
                event.projectName(), event.title(), event.dueOn(), event.occurredAt()));
    }
}
