package io.github.doriangrelu.lifemanager.reminder.domain;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.PersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.NoticePeriod;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.PersonalReminderDetails;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Recurrence;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderNote;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderStatus;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderTitle;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonalReminderTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 19);

    @Test
    void it_is_reminded_from_its_notice_period_and_while_overdue() {
        var inEightDays = reminder(TODAY.plusDays(8), Recurrence.NONE, NoticePeriod.ONE_WEEK);
        var inSevenDays = reminder(TODAY.plusDays(7), Recurrence.NONE, NoticePeriod.ONE_WEEK);
        var late = reminder(TODAY.minusDays(2), Recurrence.NONE, NoticePeriod.SAME_DAY);

        assertThat(inEightDays.reminderOn(TODAY)).isEmpty();
        assertThat(inSevenDays.reminderOn(TODAY)).hasValueSatisfying(r -> {
            assertThat(r.status()).isEqualTo(ReminderStatus.DUE_SOON);
            assertThat(r.daysLeft()).isEqualTo(7);
        });
        assertThat(late.reminderOn(TODAY)).hasValueSatisfying(r -> assertThat(r.daysLeft()).isEqualTo(-2));
    }

    @Test
    void done_ends_a_one_off_reminder_and_moves_a_recurring_one_after_today() {
        var once = reminder(TODAY, Recurrence.NONE, NoticePeriod.SAME_DAY);
        var monthlyLate = reminder(TODAY.minusMonths(3), Recurrence.MONTHLY, NoticePeriod.SAME_DAY);
        var yearlyAhead = reminder(TODAY.plusDays(3), Recurrence.YEARLY, NoticePeriod.ONE_WEEK);

        assertThat(once.complete(TODAY)).isFalse();
        assertThat(monthlyLate.complete(TODAY)).isTrue();
        assertThat(monthlyLate.details().dueOn()).isEqualTo(TODAY.plusMonths(1));
        assertThat(yearlyAhead.complete(TODAY)).isTrue();
        assertThat(yearlyAhead.details().dueOn()).isEqualTo(TODAY.plusDays(3).plusYears(1));
    }

    @Test
    void a_recurring_reminder_keeps_its_day_of_month() {
        var endOfMonth = reminder(LocalDate.of(2027, 1, 31), Recurrence.MONTHLY, NoticePeriod.SAME_DAY);

        endOfMonth.complete(LocalDate.of(2027, 1, 31));
        assertThat(endOfMonth.details().dueOn()).isEqualTo(LocalDate.of(2027, 2, 28));
        endOfMonth.complete(LocalDate.of(2027, 2, 28));
        assertThat(endOfMonth.details().dueOn()).isEqualTo(LocalDate.of(2027, 3, 31));
    }

    @Test
    void changing_only_the_note_keeps_the_day_of_month() {
        var endOfMonth = reminder(LocalDate.of(2027, 1, 31), Recurrence.MONTHLY, NoticePeriod.SAME_DAY);
        endOfMonth.complete(LocalDate.of(2027, 1, 31));

        // Même date (28 février), seule la note change : le rappel reste « du 31 ».
        var current = endOfMonth.details();
        endOfMonth.update(new PersonalReminderDetails(current.title(), ReminderNote.ofNullable("Appeler le ramoneur"),
                current.dueOn(), current.recurrence(), current.notice()));
        endOfMonth.complete(LocalDate.of(2027, 2, 28));

        assertThat(endOfMonth.details().dueOn()).isEqualTo(LocalDate.of(2027, 3, 31));
    }

    @Test
    void its_title_note_and_choices_are_checked() {
        assertThat(ReminderNote.ofNullable(" ")).isNull();
        assertThatThrownBy(() -> new ReminderTitle(" ")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new ReminderTitle("a".repeat(61))).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new ReminderNote("a".repeat(281))).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> Recurrence.of("WEEKLY")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> NoticePeriod.of("TWO_DAYS")).isInstanceOf(InvalidValueException.class);
    }

    private static PersonalReminder reminder(LocalDate dueOn, Recurrence recurrence, NoticePeriod notice) {
        return PersonalReminder.create(new HouseholdId(UUID.randomUUID()),
                new PersonalReminderDetails(new ReminderTitle("Ramonage"), null, dueOn, recurrence, notice));
    }
}
