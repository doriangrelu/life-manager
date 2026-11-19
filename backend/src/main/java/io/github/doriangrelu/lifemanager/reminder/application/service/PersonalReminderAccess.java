package io.github.doriangrelu.lifemanager.reminder.application.service;

import io.github.doriangrelu.lifemanager.reminder.application.port.out.PersonalReminderRepository;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.PersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.domain.exception.PersonalReminderNotFoundException;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.PersonalReminderId;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Code commun aux use cases qui touchent un rappel saisi : le retrouver dans le foyer du demandeur. */
@Singleton
public class PersonalReminderAccess {

    private final ReminderAccess access;
    private final PersonalReminderRepository reminders;

    PersonalReminderAccess(ReminderAccess access, PersonalReminderRepository reminders) {
        this.access = access;
        this.reminders = reminders;
    }

    /** @throws PersonalReminderNotFoundException si le rappel n'existe pas dans ce foyer */
    public PersonalReminder loadForMember(UUID householdId, UUID reminderId, String requesterId) {
        var household = access.householdOfMember(householdId, requesterId);
        return reminders.findById(new PersonalReminderId(reminderId))
                .filter(reminder -> reminder.belongsTo(household))
                .orElseThrow(PersonalReminderNotFoundException::new);
    }
}
