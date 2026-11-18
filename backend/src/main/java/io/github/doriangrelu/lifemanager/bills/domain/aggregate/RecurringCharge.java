package io.github.doriangrelu.lifemanager.bills.domain.aggregate;

import io.github.doriangrelu.lifemanager.bills.domain.event.ChargeDeadlineChanged;
import io.github.doriangrelu.lifemanager.bills.domain.exception.ChargeTerminatedException;
import io.github.doriangrelu.lifemanager.bills.domain.exception.PastFirstDueDateException;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeDetails;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeSchedule;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Charge récurrente d'un foyer (internet, électricité, assurance…).
 * Invariants : un calendrier qui ne commence pas dans le passé ; une charge résiliée ne change plus.
 * Les échéances sont des agrégats à part ({@link ChargeOccurrence}).
 */
public final class RecurringCharge extends AggregateRoot<ChargeId> {

    private final HouseholdId householdId;
    private ChargeDetails details;
    private ChargeSchedule schedule;
    private boolean active;

    private RecurringCharge(ChargeId id, HouseholdId householdId, ChargeDetails details, ChargeSchedule schedule,
                            boolean active) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer de la charge");
        this.details = Require.notNull(details, "Le détail de la charge");
        this.schedule = Require.notNull(schedule, "Le calendrier de la charge");
        this.active = active;
    }

    /** Nouvelle charge : sa première échéance est aujourd'hui ou plus tard. */
    public static RecurringCharge create(ChargeId id, HouseholdId householdId, ChargeDetails details,
                                         ChargeSchedule schedule, Clock clock) {
        ensureNotPast(schedule, clock);
        return new RecurringCharge(id, householdId, details, schedule, true);
    }

    /** Recharge une charge depuis le stockage (aucun événement produit). */
    public static RecurringCharge restore(ChargeId id, HouseholdId householdId, ChargeDetails details,
                                          ChargeSchedule schedule, boolean active) {
        return new RecurringCharge(id, householdId, details, schedule, active);
    }

    /**
     * Change la charge ; vaut pour les échéances à venir. Un nouveau calendrier ne peut pas commencer
     * dans le passé (celui déjà en place peut rester tel quel).
     */
    public void update(ChargeDetails newDetails, ChargeSchedule newSchedule, Clock clock) {
        ensureActive();
        Require.notNull(newDetails, "Le détail de la charge");
        Require.notNull(newSchedule, "Le calendrier de la charge");
        if (!newSchedule.equals(schedule)) {
            ensureNotPast(newSchedule, clock);
        }
        details = newDetails;
        schedule = newSchedule;
    }

    /** Résilie la charge : plus aucune échéance ne sera créée. */
    public void terminate() {
        ensureActive();
        active = false;
    }

    /** Annonce la prochaine échéance ouverte (ou son absence) pour que les rappels suivent. */
    public void announceDeadline(LocalDate nextDueOn, boolean awaitingConfirmation, Clock clock) {
        recordEvent(new ChargeDeadlineChanged(id(), householdId, details.name(), nextDueOn, awaitingConfirmation,
                clock.instant()));
    }

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    private void ensureActive() {
        if (!active) {
            throw new ChargeTerminatedException();
        }
    }

    private static void ensureNotPast(ChargeSchedule schedule, Clock clock) {
        if (schedule.firstDueOn().isBefore(LocalDate.now(clock))) {
            throw new PastFirstDueDateException();
        }
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public ChargeDetails details() {
        return details;
    }

    public ChargeSchedule schedule() {
        return schedule;
    }

    public boolean active() {
        return active;
    }
}
