package io.github.doriangrelu.lifemanager.budget.domain.aggregate;

import io.github.doriangrelu.lifemanager.budget.domain.exception.FutureIncomeDateException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Rentrée d'argent d'un foyer (salaire, prime, remboursement…). Pendant d'une dépense, de signe inverse.
 * Invariants : montant strictement positif et plafonné ({@link IncomeDetails}), jamais datée d'un jour à venir.
 * Catégorie de rentrée et bénéficiaire membre du foyer sont vérifiés par l'application.
 */
public final class Income extends AggregateRoot<IncomeId> {

    private final HouseholdId householdId;
    private IncomeDetails details;
    private final MemberId recordedBy;
    private final Instant recordedAt;

    private Income(IncomeId id, HouseholdId householdId, IncomeDetails details, MemberId recordedBy,
                   Instant recordedAt) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer de la rentrée");
        this.details = Require.notNull(details, "Le détail de la rentrée");
        this.recordedBy = Require.notNull(recordedBy, "L'auteur de la saisie");
        this.recordedAt = Require.notNull(recordedAt, "La date de saisie");
    }

    /** Enregistre une nouvelle rentrée ; {@code clock} donne le jour courant pour refuser une date future. */
    public static Income record(IncomeId id, HouseholdId householdId, IncomeDetails details, MemberId recordedBy,
                                Clock clock) {
        ensureNotInFuture(details, clock);
        return new Income(id, householdId, details, recordedBy, clock.instant());
    }

    /** Recharge une rentrée depuis le stockage. */
    public static Income restore(IncomeId id, HouseholdId householdId, IncomeDetails details, MemberId recordedBy,
                                 Instant recordedAt) {
        return new Income(id, householdId, details, recordedBy, recordedAt);
    }

    /** Remplace tout ce qui a été saisi, avec les mêmes règles qu'à la saisie. */
    public void correct(IncomeDetails newDetails, Clock clock) {
        Require.notNull(newDetails, "Le détail de la rentrée");
        ensureNotInFuture(newDetails, clock);
        details = newDetails;
    }

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    private static void ensureNotInFuture(IncomeDetails details, Clock clock) {
        if (details.receivedOn().isAfter(LocalDate.now(clock))) {
            throw new FutureIncomeDateException();
        }
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public IncomeDetails details() {
        return details;
    }

    public MemberId recordedBy() {
        return recordedBy;
    }

    public Instant recordedAt() {
        return recordedAt;
    }
}
