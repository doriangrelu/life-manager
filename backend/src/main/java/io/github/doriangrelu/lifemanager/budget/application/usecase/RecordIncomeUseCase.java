package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordIncome;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordIncomeCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.IncomeView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.IncomeRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.BudgetAccess;
import io.github.doriangrelu.lifemanager.budget.application.service.IncomeRules;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Income;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Enregistre une rentrée saisie par un membre du foyer. */
@Singleton
class RecordIncomeUseCase implements RecordIncome {

    private final BudgetAccess access;
    private final IncomeRules rules;
    private final IncomeRepository incomes;
    private final Clock clock;

    RecordIncomeUseCase(BudgetAccess access, IncomeRules rules, IncomeRepository incomes, Clock clock) {
        this.access = access;
        this.rules = rules;
        this.incomes = incomes;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public IncomeView record(RecordIncomeCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var details = rules.validDetails(household, command.input());
        var income = Income.record(IncomeId.newId(), household, details, MemberId.of(command.requesterId()), clock);
        incomes.save(income);
        return IncomeView.from(income);
    }
}
