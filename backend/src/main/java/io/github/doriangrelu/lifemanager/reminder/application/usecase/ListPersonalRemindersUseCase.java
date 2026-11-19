package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.ListPersonalReminders;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.query.ListRemindersQuery;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.PersonalReminderView;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.PersonalReminderRepository;
import io.github.doriangrelu.lifemanager.reminder.application.service.ReminderAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Donne à un membre les rappels saisis de son foyer, du plus proche au plus lointain. */
@Singleton
class ListPersonalRemindersUseCase implements ListPersonalReminders {

    private final ReminderAccess access;
    private final PersonalReminderRepository reminders;

    ListPersonalRemindersUseCase(ReminderAccess access, PersonalReminderRepository reminders) {
        this.access = access;
        this.reminders = reminders;
    }

    @Override
    @Transactional(transactionManager = "reminder", readOnly = true)
    public List<PersonalReminderView> list(ListRemindersQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        return reminders.findAllByHousehold(household).stream().map(PersonalReminderView::from).toList();
    }
}
