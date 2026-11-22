package io.github.doriangrelu.lifemanager.vehicle.application.service;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.BudgetInput;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceExpenseRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceExpense;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.exception.AlreadyInBudgetException;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.ExpenseTarget;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Envoie le coût d'une opération au budget et le retire à sa suppression (à appeler dans une transaction). */
@Singleton
public class MaintenanceExpenseRecorder {

    private final HouseholdMembers members;
    private final MaintenanceExpenseRepository expenses;
    private final DomainEventPublisher events;
    private final Clock clock;

    MaintenanceExpenseRecorder(HouseholdMembers members, MaintenanceExpenseRepository expenses,
                               DomainEventPublisher events, Clock clock) {
        this.members = members;
        this.expenses = expenses;
        this.events = events;
        this.clock = clock;
    }

    /**
     * Vérifie le choix avant tout enregistrement.
     *
     * @throws InvalidValueException si le payeur n'est pas membre du foyer
     */
    public ExpenseTarget target(Vehicle vehicle, BudgetInput input) {
        if (input.categoryId() == null || input.payerId() == null) {
            throw new InvalidValueException("Choisissez la catégorie et qui a payé");
        }
        var payer = new MemberId(input.payerId());
        if (!members.isMember(vehicle.householdId(), payer)) {
            throw new InvalidValueException("Le payeur doit être membre du foyer");
        }
        return new ExpenseTarget(input.categoryId(), payer);
    }

    /** @throws AlreadyInBudgetException si l'opération est déjà dans le budget */
    public void send(MaintenanceEntry entry, Vehicle vehicle, ExpenseTarget target) {
        if (expenses.findById(entry.id()).isPresent()) {
            throw new AlreadyInBudgetException();
        }
        var expense = MaintenanceExpense.send(entry, vehicle, target, clock);
        expenses.save(expense);
        events.publishEventsOf(expense);
    }

    /** Retire la dépense de l'opération, si elle avait été envoyée au budget. */
    public void withdraw(MaintenanceEntry entry) {
        expenses.findById(entry.id()).ifPresent(expense -> {
            expense.withdraw(clock);
            expenses.delete(expense);
            events.publishEventsOf(expense);
        });
    }
}
