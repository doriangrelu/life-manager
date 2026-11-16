package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.ListReminders;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.query.ListRemindersQuery;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.ReminderView;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.DeadlineRepository;
import io.github.doriangrelu.lifemanager.reminder.application.service.ReminderAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/** Donne à un membre les rappels du jour pour son foyer (échéances passées ou proches). */
@Singleton
class ListRemindersUseCase implements ListReminders {

    private final ReminderAccess access;
    private final DeadlineRepository deadlines;
    private final Clock clock;

    ListRemindersUseCase(ReminderAccess access, DeadlineRepository deadlines, Clock clock) {
        this.access = access;
        this.deadlines = deadlines;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "reminder", readOnly = true)
    public List<ReminderView> list(ListRemindersQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        var today = LocalDate.now(clock);
        return deadlines.findAllByHousehold(household).stream()
                .flatMap(deadline -> deadline.reminderOn(today).map(r -> ReminderView.of(deadline, r)).stream())
                .toList();
    }
}
