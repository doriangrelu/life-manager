package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.ListReminders;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.query.ListRemindersQuery;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.ReminderView;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.DeadlineRepository;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.PersonalReminderRepository;
import io.github.doriangrelu.lifemanager.reminder.application.service.ReminderAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Donne à un membre les rappels du jour pour son foyer : échéances passées ou proches (véhicules, charges)
 * et rappels saisis par les membres, de la date la plus ancienne à la plus lointaine.
 */
@Singleton
class ListRemindersUseCase implements ListReminders {

    private final ReminderAccess access;
    private final DeadlineRepository deadlines;
    private final PersonalReminderRepository personal;
    private final Clock clock;

    ListRemindersUseCase(ReminderAccess access, DeadlineRepository deadlines, PersonalReminderRepository personal,
                         Clock clock) {
        this.access = access;
        this.deadlines = deadlines;
        this.personal = personal;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "reminder", readOnly = true)
    public List<ReminderView> list(ListRemindersQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        var today = LocalDate.now(clock);
        var fromDeadlines = deadlines.findAllByHousehold(household).stream()
                .flatMap(deadline -> deadline.reminderOn(today).map(r -> ReminderView.of(deadline, r)).stream());
        var fromMembers = personal.findAllByHousehold(household).stream()
                .flatMap(saved -> saved.reminderOn(today).map(r -> ReminderView.of(saved, r)).stream());
        return Stream.concat(fromDeadlines, fromMembers)
                .sorted(Comparator.comparing(ReminderView::dueOn).thenComparing(ReminderView::subject))
                .toList();
    }
}
