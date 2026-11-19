package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.RemoveReminderDone;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.RemoveReminderDoneCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.ReminderDoneRepository;
import io.github.doriangrelu.lifemanager.reminder.application.service.ReminderAccess;
import io.github.doriangrelu.lifemanager.reminder.domain.exception.ReminderDoneNotFoundException;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderDoneId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Retire une ligne de l'historique ; tout membre du foyer peut le faire. */
@Singleton
class RemoveReminderDoneUseCase implements RemoveReminderDone {

    private final ReminderAccess access;
    private final ReminderDoneRepository history;

    RemoveReminderDoneUseCase(ReminderAccess access, ReminderDoneRepository history) {
        this.access = access;
        this.history = history;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public void remove(RemoveReminderDoneCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var entry = history.findById(new ReminderDoneId(command.entryId()))
                .filter(found -> found.belongsTo(household))
                .orElseThrow(ReminderDoneNotFoundException::new);
        history.delete(entry.id());
    }
}
