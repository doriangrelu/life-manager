package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.PurgeReminderHistory;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.ReminderDoneRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.ZonedDateTime;

/** Ménage quotidien : l'historique ne garde qu'un an (tâche interne, sans foyer à contrôler). */
@Singleton
class PurgeReminderHistoryUseCase implements PurgeReminderHistory {

    private final ReminderDoneRepository history;
    private final Clock clock;

    PurgeReminderHistoryUseCase(ReminderDoneRepository history, Clock clock) {
        this.history = history;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public void purge() {
        history.deleteDoneBefore(ZonedDateTime.now(clock).minusYears(1).toInstant());
    }
}
