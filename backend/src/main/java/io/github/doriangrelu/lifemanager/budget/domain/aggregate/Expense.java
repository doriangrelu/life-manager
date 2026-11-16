package io.github.doriangrelu.lifemanager.budget.domain.aggregate;

import io.github.doriangrelu.lifemanager.budget.domain.event.ExpenseCorrected;
import io.github.doriangrelu.lifemanager.budget.domain.event.ExpenseDeleted;
import io.github.doriangrelu.lifemanager.budget.domain.event.ExpenseRecorded;
import io.github.doriangrelu.lifemanager.budget.domain.exception.FutureExpenseDateException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Dépense d'un foyer. Invariants : montant strictement positif et plafonné (garantis par {@link ExpenseDetails}),
 * jamais datée d'un jour à venir. Les règles qui la relient à d'autres objets (catégorie active,
 * payeur membre du foyer) sont vérifiées par l'application.
 */
public final class Expense extends AggregateRoot<ExpenseId> {

    private final HouseholdId householdId;
    private ExpenseDetails details;
    private final MemberId recordedBy;
    private final Instant recordedAt;

    private Expense(ExpenseId id, HouseholdId householdId, ExpenseDetails details, MemberId recordedBy,
                    Instant recordedAt) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer de la dépense");
        this.details = Require.notNull(details, "Le détail de la dépense");
        this.recordedBy = Require.notNull(recordedBy, "L'auteur de la saisie");
        this.recordedAt = Require.notNull(recordedAt, "La date de saisie");
    }

    /** Saisit une nouvelle dépense ; {@code clock} donne le jour courant pour refuser une date future. */
    public static Expense record(ExpenseId id, HouseholdId householdId, ExpenseDetails details, MemberId recordedBy,
                                 Clock clock) {
        ensureNotInFuture(details, clock);
        var expense = new Expense(id, householdId, details, recordedBy, clock.instant());
        expense.recordEvent(new ExpenseRecorded(id, householdId, clock.instant()));
        return expense;
    }

    /** Recharge une dépense depuis le stockage (aucun événement produit). */
    public static Expense restore(ExpenseId id, HouseholdId householdId, ExpenseDetails details, MemberId recordedBy,
                                  Instant recordedAt) {
        return new Expense(id, householdId, details, recordedBy, recordedAt);
    }

    /** Remplace tout ce qui a été saisi, avec les mêmes règles qu'à la saisie. */
    public void correct(ExpenseDetails newDetails, Clock clock) {
        Require.notNull(newDetails, "Le détail de la dépense");
        ensureNotInFuture(newDetails, clock);
        details = newDetails;
        recordEvent(new ExpenseCorrected(id(), householdId, clock.instant()));
    }

    /** À appeler juste avant de supprimer la dépense, pour annoncer sa suppression. */
    public void markDeleted(Clock clock) {
        recordEvent(new ExpenseDeleted(id(), householdId, clock.instant()));
    }

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    private static void ensureNotInFuture(ExpenseDetails details, Clock clock) {
        if (details.spentOn().isAfter(LocalDate.now(clock))) {
            throw new FutureExpenseDateException();
        }
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public ExpenseDetails details() {
        return details;
    }

    public MemberId recordedBy() {
        return recordedBy;
    }

    public Instant recordedAt() {
        return recordedAt;
    }
}
