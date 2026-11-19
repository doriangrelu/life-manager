package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.CompletePersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.PersonalReminderCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.PersonalReminderRepository;
import io.github.doriangrelu.lifemanager.reminder.application.service.PersonalReminderAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.LocalDate;

/** « C'est fait » : un rappel ponctuel est supprimé, un rappel récurrent passe à sa date suivante. */
@Singleton
class CompletePersonalReminderUseCase implements CompletePersonalReminder {

    private final PersonalReminderAccess access;
    private final PersonalReminderRepository reminders;
    private final Clock clock;

    CompletePersonalReminderUseCase(PersonalReminderAccess access, PersonalReminderRepository reminders,
                                    Clock clock) {
        this.access = access;
        this.reminders = reminders;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public void complete(PersonalReminderCommand command) {
        var reminder = access.loadForMember(command.householdId(), command.reminderId(), command.requesterId());
        if (reminder.complete(LocalDate.now(clock))) {
            reminders.save(reminder);
        } else {
            reminders.delete(reminder.id());
        }
    }
}
