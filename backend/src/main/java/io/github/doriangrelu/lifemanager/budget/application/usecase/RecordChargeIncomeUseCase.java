package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordChargeIncome;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordChargeExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ChargeIncomeLinks;
import io.github.doriangrelu.lifemanager.budget.application.port.out.IncomeRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.ChargeIncomeDetails;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Income;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/**
 * Enregistre la rentrée d'une échéance de rentrée récurrente arrivée. L'échéance est réservée avant la création :
 * reçue deux fois, elle ne produit qu'une rentrée ; supprimée par un membre, la rentrée ne revient pas.
 */
@Singleton
class RecordChargeIncomeUseCase implements RecordChargeIncome {

    private final ChargeIncomeDetails details;
    private final ChargeIncomeLinks links;
    private final IncomeRepository incomes;
    private final Clock clock;

    RecordChargeIncomeUseCase(ChargeIncomeDetails details, ChargeIncomeLinks links, IncomeRepository incomes,
                              Clock clock) {
        this.details = details;
        this.links = links;
        this.incomes = incomes;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public void record(RecordChargeExpenseCommand command) {
        var incomeDetails = details.of(command);
        var incomeId = IncomeId.newId();
        if (incomeDetails.isEmpty() || !links.claim(command.occurrenceId(), incomeId)) {
            return; // montant nul, ou rentrée déjà créée pour cette échéance
        }
        incomes.save(Income.record(incomeId, new HouseholdId(command.householdId()), incomeDetails.get(),
                new MemberId(command.payerId()), clock));
    }
}
