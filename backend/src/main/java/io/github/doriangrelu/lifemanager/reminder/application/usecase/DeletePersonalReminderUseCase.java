package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.DeletePersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.PersonalReminderCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.PersonalReminderRepository;
import io.github.doriangrelu.lifemanager.reminder.application.service.PersonalReminderAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Supprime un rappel saisi du foyer. */
@Singleton
class DeletePersonalReminderUseCase implements DeletePersonalReminder {

    private final PersonalReminderAccess access;
    private final PersonalReminderRepository reminders;

    DeletePersonalReminderUseCase(PersonalReminderAccess access, PersonalReminderRepository reminders) {
        this.access = access;
        this.reminders = reminders;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public void delete(PersonalReminderCommand command) {
        var reminder = access.loadForMember(command.householdId(), command.reminderId(), command.requesterId());
        reminders.delete(reminder.id());
    }
}
