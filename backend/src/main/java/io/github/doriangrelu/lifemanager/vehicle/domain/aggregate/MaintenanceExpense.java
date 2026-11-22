package io.github.doriangrelu.lifemanager.vehicle.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.MaintenanceExpenseSent;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.MaintenanceExpenseWithdrawn;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Cost;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.ExpenseTarget;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceOtherLabel;

import java.time.Clock;

/**
 * Coût d'une opération du carnet envoyé au budget (même identifiant que l'opération).
 * Invariant : seule une opération avec un coût non nul peut être envoyée.
 */
public final class MaintenanceExpense extends AggregateRoot<MaintenanceEntryId> {

    private final HouseholdId householdId;
    private final ExpenseTarget target;

    private MaintenanceExpense(MaintenanceEntryId id, HouseholdId householdId, ExpenseTarget target) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer");
        this.target = Require.notNull(target, "La destination dans le budget");
    }

    /** Envoie le coût de l'opération au budget ; le libellé de la dépense est « type · véhicule ». */
    public static MaintenanceExpense send(MaintenanceEntry entry, Vehicle vehicle, ExpenseTarget target,
                                         Clock clock) {
        var cost = entry.details().optionalCost().map(Cost::cents).orElse(0L);
        if (cost == 0) {
            throw new InvalidValueException("Cette opération n'a pas de coût à ajouter au budget");
        }
        var expense = new MaintenanceExpense(entry.id(), vehicle.householdId(), target);
        expense.recordEvent(new MaintenanceExpenseSent(entry.id(), vehicle.householdId(), label(entry, vehicle),
                cost, entry.details().performedOn(), target, clock.instant()));
        return expense;
    }

    /** Recharge depuis le stockage (aucun événement produit). */
    public static MaintenanceExpense restore(MaintenanceEntryId id, HouseholdId householdId, ExpenseTarget target) {
        return new MaintenanceExpense(id, householdId, target);
    }

    /** À appeler quand l'opération est supprimée : sa dépense part aussi. */
    public void withdraw(Clock clock) {
        recordEvent(new MaintenanceExpenseWithdrawn(id(), householdId, clock.instant()));
    }

    private static String label(MaintenanceEntry entry, Vehicle vehicle) {
        var details = entry.details();
        var kind = details.optionalOtherLabel().map(MaintenanceOtherLabel::value).orElse(details.kind().label());
        return kind + " · " + vehicle.details().name().value();
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public ExpenseTarget target() {
        return target;
    }
}
