package io.github.doriangrelu.lifemanager.bills.domain.aggregate;

import io.github.doriangrelu.lifemanager.bills.domain.event.OccurrencePaid;
import io.github.doriangrelu.lifemanager.bills.domain.exception.OccurrenceAlreadySettledException;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Amount;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.AmountKind;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Due;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceStatus;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Settlement;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Une échéance d'une charge. Cycle : à venir → (à confirmer) → payée, ou sautée.
 * Invariant : une échéance payée ou sautée ne change plus. Le paiement produit {@link OccurrencePaid},
 * dont le budget fait une dépense.
 */
public final class ChargeOccurrence extends AggregateRoot<OccurrenceId> {

    private final ChargeId chargeId;
    private final HouseholdId householdId;
    private final Due due;
    private Settlement settlement;

    private ChargeOccurrence(OccurrenceId id, ChargeId chargeId, HouseholdId householdId, Due due,
                             Settlement settlement) {
        super(id);
        this.chargeId = Require.notNull(chargeId, "La charge de l'échéance");
        this.householdId = Require.notNull(householdId, "Le foyer de l'échéance");
        this.due = Require.notNull(due, "L'échéance");
        this.settlement = Require.notNull(settlement, "Le statut de l'échéance");
    }

    /** Prévoit l'échéance d'une charge à cette date, au montant prévu de la charge. */
    public static ChargeOccurrence plan(OccurrenceId id, RecurringCharge charge, LocalDate dueOn) {
        return new ChargeOccurrence(id, charge.id(), charge.householdId(), new Due(dueOn, charge.details().amount()),
                Settlement.UPCOMING);
    }

    /** Recharge une échéance depuis le stockage (aucun événement produit). */
    public static ChargeOccurrence restore(OccurrenceId id, ChargeId chargeId, HouseholdId householdId, Due due,
                                           Settlement settlement) {
        return new ChargeOccurrence(id, chargeId, householdId, due, settlement);
    }

    /**
     * Le jour de l'échéance est arrivé : montant fixe → payée au montant prévu ; montant variable → à confirmer.
     * Sans effet sur une échéance déjà traitée.
     */
    public void arrive(RecurringCharge charge, Clock clock) {
        if (settlement.status() != OccurrenceStatus.UPCOMING) {
            return;
        }
        if (charge.details().kind() == AmountKind.FIXED) {
            pay(due.expected(), charge, clock);
        } else {
            settlement = Settlement.TO_CONFIRM;
        }
    }

    /** Paie l'échéance au montant réel (facture reçue), même avant sa date. */
    public void confirm(Amount amount, RecurringCharge charge, Clock clock) {
        ensureOpen();
        pay(Require.notNull(amount, "Le montant payé"), charge, clock);
    }

    /** Saute l'échéance (mois offert, résiliation en cours) : aucune dépense. */
    public void skip() {
        ensureOpen();
        settlement = Settlement.SKIPPED;
    }

    public boolean belongsTo(ChargeId charge) {
        return chargeId.equals(charge);
    }

    /** Jour du paiement : la date d'échéance, ou aujourd'hui si on paie en avance (jamais une date future). */
    private void pay(Amount amount, RecurringCharge charge, Clock clock) {
        var today = LocalDate.now(clock);
        var paidOn = due.dueOn().isAfter(today) ? today : due.dueOn();
        settlement = Settlement.paid(amount, paidOn);
        recordEvent(new OccurrencePaid(id(), householdId, charge.details(), amount, paidOn, clock.instant()));
    }

    private void ensureOpen() {
        if (!settlement.status().isOpen()) {
            throw new OccurrenceAlreadySettledException();
        }
    }

    public ChargeId chargeId() {
        return chargeId;
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public Due due() {
        return due;
    }

    public Settlement settlement() {
        return settlement;
    }
}
