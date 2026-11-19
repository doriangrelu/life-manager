package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.UpdatePersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.UpdatePersonalReminderCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.PersonalReminderView;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.PersonalReminderRepository;
import io.github.doriangrelu.lifemanager.reminder.application.service.PersonalReminderAccess;
import io.github.doriangrelu.lifemanager.reminder.application.service.PersonalReminderInputs;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Modifie un rappel saisi du foyer. */
@Singleton
class UpdatePersonalReminderUseCase implements UpdatePersonalReminder {

    private final PersonalReminderAccess access;
    private final PersonalReminderRepository reminders;

    UpdatePersonalReminderUseCase(PersonalReminderAccess access, PersonalReminderRepository reminders) {
        this.access = access;
        this.reminders = reminders;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public PersonalReminderView update(UpdatePersonalReminderCommand command) {
        var reminder = access.loadForMember(command.householdId(), command.reminderId(), command.requesterId());
        reminder.update(PersonalReminderInputs.details(command.input()));
        reminders.save(reminder);
        return PersonalReminderView.from(reminder);
    }
}
