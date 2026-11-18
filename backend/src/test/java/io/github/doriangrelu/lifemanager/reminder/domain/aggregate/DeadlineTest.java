package io.github.doriangrelu.lifemanager.reminder.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineDetails;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineKind;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Reminder;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderStatus;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.SourceId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Subject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeadlineTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 16);

    @Test
    void a_past_deadline_is_overdue() {
        assertThat(dueOn(LocalDate.of(2026, 10, 1)).reminderOn(TODAY))
                .contains(new Reminder(ReminderStatus.OVERDUE, -46));
        assertThat(dueOn(TODAY.minusDays(1)).reminderOn(TODAY)).contains(new Reminder(ReminderStatus.OVERDUE, -1));
    }

    @Test
    void a_deadline_within_30_days_is_due_soon() {
        assertThat(dueOn(TODAY).reminderOn(TODAY)).contains(new Reminder(ReminderStatus.DUE_SOON, 0));
        assertThat(dueOn(TODAY.plusDays(30)).reminderOn(TODAY)).contains(new Reminder(ReminderStatus.DUE_SOON, 30));
    }

    @Test
    void a_later_deadline_gives_no_reminder() {
        assertThat(dueOn(TODAY.plusDays(31)).reminderOn(TODAY)).isEmpty();
    }

    @Test
    void a_deadline_needs_a_readable_subject_and_a_known_kind() {
        assertThatThrownBy(() -> new Subject(" ")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new Subject("a".repeat(61))).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> DeadlineKind.of("INSURANCE")).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void a_charge_is_reminded_7_days_ahead_and_while_it_awaits_its_amount() {
        var household = new HouseholdId(UUID.randomUUID());
        var source = new SourceId(UUID.randomUUID());
        var charge = Deadline.track(household, source,
                new DeadlineDetails(DeadlineKind.CHARGE, new Subject("Internet"), TODAY.plusDays(7)));
        var later = Deadline.track(household, source,
                new DeadlineDetails(DeadlineKind.CHARGE, new Subject("Internet"), TODAY.plusDays(8)));
        var toConfirm = Deadline.track(household, source,
                new DeadlineDetails(DeadlineKind.CHARGE_TO_CONFIRM, new Subject("Électricité"), TODAY.minusDays(40)));

        assertThat(charge.reminderOn(TODAY)).isPresent();
        assertThat(later.reminderOn(TODAY)).isEmpty();
        assertThat(toConfirm.reminderOn(TODAY)).isPresent();
    }

    private static Deadline dueOn(LocalDate day) {
        return Deadline.track(new HouseholdId(UUID.randomUUID()), new SourceId(UUID.randomUUID()),
                new DeadlineDetails(DeadlineKind.SERVICE, new Subject("Clio"), day));
    }
}
