package io.github.doriangrelu.lifemanager.bills.domain;

import io.github.doriangrelu.lifemanager.bills.domain.aggregate.ChargeOccurrence;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.RecurringCharge;
import io.github.doriangrelu.lifemanager.bills.domain.event.OccurrencePaid;
import io.github.doriangrelu.lifemanager.bills.domain.exception.ChargeTerminatedException;
import io.github.doriangrelu.lifemanager.bills.domain.exception.OccurrenceAlreadySettledException;
import io.github.doriangrelu.lifemanager.bills.domain.exception.PastFirstDueDateException;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Amount;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.AmountKind;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeDetails;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeName;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeSchedule;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Frequency;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceStatus;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChargeDomainTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 18);
    private static final Clock CLOCK = Clock.fixed(TODAY.atStartOfDay(ZoneId.of("Europe/Paris")).toInstant(),
            ZoneId.of("Europe/Paris"));
    private static final HouseholdId HOUSEHOLD = new HouseholdId(UUID.randomUUID());

    @Test
    void each_due_date_is_computed_from_the_first_one() {
        var monthly = new ChargeSchedule(LocalDate.of(2027, 1, 31), Frequency.MONTHLY);

        assertThat(monthly.dueOn(1)).isEqualTo(LocalDate.of(2027, 2, 28));
        assertThat(monthly.dueOn(2)).isEqualTo(LocalDate.of(2027, 3, 31));
        assertThat(new ChargeSchedule(TODAY, Frequency.QUARTERLY).dueOn(1)).isEqualTo(LocalDate.of(2027, 2, 18));
        assertThat(new ChargeSchedule(TODAY, Frequency.YEARLY).dueOn(2)).isEqualTo(LocalDate.of(2028, 11, 18));
    }

    @Test
    void names_are_normalized_and_amounts_bounded() {
        assertThat(new ChargeName("  Box   INTERNET ").value()).isEqualTo("box internet");
        assertThatThrownBy(() -> new ChargeName("a".repeat(61))).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> Amount.ofCents(-1)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> Amount.ofCents(10_000_001)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> Frequency.of("WEEKLY")).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void a_charge_cannot_start_in_the_past() {
        assertThatThrownBy(() -> charge(AmountKind.FIXED, TODAY.minusDays(1)))
                .isInstanceOf(PastFirstDueDateException.class);
    }

    @Test
    void a_terminated_charge_no_longer_changes() {
        var charge = charge(AmountKind.FIXED, TODAY);
        charge.terminate();

        assertThat(charge.active()).isFalse();
        assertThatThrownBy(() -> charge.update(charge.details(), charge.schedule(), CLOCK))
                .isInstanceOf(ChargeTerminatedException.class);
    }

    @Test
    void a_fixed_charge_is_paid_on_its_due_date() {
        var charge = charge(AmountKind.FIXED, TODAY);
        var occurrence = ChargeOccurrence.plan(OccurrenceId.newId(), charge, TODAY);

        occurrence.arrive(charge, CLOCK);

        assertThat(occurrence.settlement().status()).isEqualTo(OccurrenceStatus.PAID);
        assertThat(occurrence.settlement().paid()).isEqualTo(Amount.ofCents(3999));
        assertThat(occurrence.pullEvents()).singleElement().isInstanceOf(OccurrencePaid.class);
    }

    @Test
    void a_variable_charge_waits_for_its_real_amount() {
        var charge = charge(AmountKind.VARIABLE, TODAY);
        var occurrence = ChargeOccurrence.plan(OccurrenceId.newId(), charge, TODAY);

        occurrence.arrive(charge, CLOCK);
        assertThat(occurrence.settlement().status()).isEqualTo(OccurrenceStatus.TO_CONFIRM);
        assertThat(occurrence.pullEvents()).isEmpty();

        occurrence.confirm(Amount.ofCents(9240), charge, CLOCK);
        assertThat(occurrence.settlement().paid()).isEqualTo(Amount.ofCents(9240));
        assertThat(occurrence.settlement().paidOn()).isEqualTo(TODAY);
    }

    @Test
    void paying_in_advance_is_dated_today_and_a_settled_occurrence_is_final() {
        var charge = charge(AmountKind.VARIABLE, TODAY);
        var next = ChargeOccurrence.plan(OccurrenceId.newId(), charge, TODAY.plusDays(10));

        next.confirm(Amount.ofCents(5000), charge, CLOCK);

        assertThat(next.settlement().paidOn()).isEqualTo(TODAY);
        assertThatThrownBy(next::skip).isInstanceOf(OccurrenceAlreadySettledException.class);
        assertThatThrownBy(() -> next.confirm(Amount.ofCents(1), charge, CLOCK))
                .isInstanceOf(OccurrenceAlreadySettledException.class);
    }

    @Test
    void a_skipped_occurrence_produces_no_payment() {
        var charge = charge(AmountKind.FIXED, TODAY);
        var occurrence = ChargeOccurrence.plan(OccurrenceId.newId(), charge, TODAY.plusMonths(1));

        occurrence.skip();
        occurrence.arrive(charge, CLOCK);

        assertThat(occurrence.settlement().status()).isEqualTo(OccurrenceStatus.SKIPPED);
        assertThat(occurrence.pullEvents()).isEmpty();
    }

    private static RecurringCharge charge(AmountKind kind, LocalDate firstDueOn) {
        var details = new ChargeDetails(new ChargeName("Internet"), Amount.ofCents(3999), kind, UUID.randomUUID(),
                new MemberId(UUID.randomUUID()), null);
        return RecurringCharge.create(ChargeId.newId(), HOUSEHOLD, details,
                new ChargeSchedule(firstDueOn, Frequency.MONTHLY), CLOCK);
    }
}
