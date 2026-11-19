package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.CompletePersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.PersonalReminderCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.PersonalReminderRepository;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.ReminderDoneRepository;
import io.github.doriangrelu.lifemanager.reminder.application.service.PersonalReminderAccess;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.ReminderDone;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.LocalDate;

/**
 * « C'est fait » : une ligne est ajoutée à l'historique, puis un rappel ponctuel est supprimé
 * et un rappel récurrent passe à sa date suivante.
 */
@Singleton
class CompletePersonalReminderUseCase implements CompletePersonalReminder {

    private final PersonalReminderAccess access;
    private final PersonalReminderRepository reminders;
    private final ReminderDoneRepository history;
    private final Clock clock;

    CompletePersonalReminderUseCase(PersonalReminderAccess access, PersonalReminderRepository reminders,
                                    ReminderDoneRepository history, Clock clock) {
        this.access = access;
        this.reminders = reminders;
        this.history = history;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public void complete(PersonalReminderCommand command) {
        var reminder = access.loadForMember(command.householdId(), command.reminderId(), command.requesterId());
        // La trace d'abord : elle garde la date prévue avant que le rappel avance.
        history.save(ReminderDone.of(reminder, MemberId.of(command.requesterId()), clock.instant()));
        if (reminder.complete(LocalDate.now(clock))) {
            reminders.save(reminder);
        } else {
            reminders.delete(reminder.id());
        }
    }
}
