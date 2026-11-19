package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.Authorship;
import jakarta.inject.Named;
import java.util.UUID;
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
    private final Authorship authorship;

    AddPersonalReminderUseCase(ReminderAccess access, PersonalReminderRepository reminders,
                               @Named("reminder") Authorship authorship) {
        this.authorship = authorship;
        this.access = access;
        this.reminders = reminders;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public PersonalReminderView add(AddPersonalReminderCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var reminder = PersonalReminder.create(household, PersonalReminderInputs.details(command.input()));
        reminders.save(reminder);
        authorship.record(reminder.id().value(), household.value(), UUID.fromString(command.requesterId()));
        return PersonalReminderView.from(reminder);
    }
}
