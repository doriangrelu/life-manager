package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.CorrectIncome;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CorrectIncomeCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.IncomeView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.IncomeRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.IncomeAccess;
import io.github.doriangrelu.lifemanager.budget.application.service.IncomeRules;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Corrige une rentrée : mêmes règles qu'à la saisie, et rien ne change si la correction est refusée. */
@Singleton
class CorrectIncomeUseCase implements CorrectIncome {

    private final IncomeAccess incomeAccess;
    private final IncomeRules rules;
    private final IncomeRepository incomes;
    private final Clock clock;

    CorrectIncomeUseCase(IncomeAccess incomeAccess, IncomeRules rules, IncomeRepository incomes, Clock clock) {
        this.incomeAccess = incomeAccess;
        this.rules = rules;
        this.incomes = incomes;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public IncomeView correct(CorrectIncomeCommand command) {
        var income = incomeAccess.loadForMember(command.householdId(), command.incomeId(), command.requesterId());
        income.correct(rules.validDetails(income.householdId(), command.input()), clock);
        incomes.save(income);
        return IncomeView.from(income);
    }
}
