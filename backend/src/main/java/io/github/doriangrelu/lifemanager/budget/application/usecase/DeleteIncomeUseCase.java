package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.DeleteIncome;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.DeleteIncomeCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.out.IncomeRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.IncomeAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Supprime définitivement une rentrée du foyer. */
@Singleton
class DeleteIncomeUseCase implements DeleteIncome {

    private final IncomeAccess incomeAccess;
    private final IncomeRepository incomes;

    DeleteIncomeUseCase(IncomeAccess incomeAccess, IncomeRepository incomes) {
        this.incomeAccess = incomeAccess;
        this.incomes = incomes;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public void delete(DeleteIncomeCommand command) {
        incomes.delete(incomeAccess.loadForMember(command.householdId(), command.incomeId(), command.requesterId()));
    }
}
