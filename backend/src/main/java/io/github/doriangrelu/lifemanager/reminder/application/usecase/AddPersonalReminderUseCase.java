package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.AddPersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.AddPersonalReminderCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.PersonalReminderView;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.PersonalReminderRepository;
import io.github.doriangrelu.lifemanager.reminder.application.service.PersonalReminderInputs;
import io.github.doriangrelu.lifemanager.reminder.application.service.ReminderAccess;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.PersonalReminder;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Ajoute un rappel saisi par un membre. */
@Singleton
class AddPersonalReminderUseCase implements AddPersonalReminder {

    private final ReminderAccess access;
    private final PersonalReminderRepository reminders;

    AddPersonalReminderUseCase(ReminderAccess access, PersonalReminderRepository reminders) {
        this.access = access;
        this.reminders = reminders;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public PersonalReminderView add(AddPersonalReminderCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var reminder = PersonalReminder.create(household, PersonalReminderInputs.details(command.input()));
        reminders.save(reminder);
        return PersonalReminderView.from(reminder);
    }
}
