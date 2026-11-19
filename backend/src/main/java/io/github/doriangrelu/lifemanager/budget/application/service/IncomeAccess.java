package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.out.IncomeRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Income;
import io.github.doriangrelu.lifemanager.budget.domain.exception.IncomeNotFoundException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeId;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Retrouve une rentrée d'un foyer pour l'un de ses membres (correction, suppression). */
@Singleton
public class IncomeAccess {

    private final BudgetAccess access;
    private final IncomeRepository incomes;

    IncomeAccess(BudgetAccess access, IncomeRepository incomes) {
        this.access = access;
        this.incomes = incomes;
    }

    /** @throws IncomeNotFoundException si la rentrée n'existe pas dans ce foyer */
    public Income loadForMember(UUID householdId, UUID incomeId, String requesterId) {
        var household = access.householdOfMember(householdId, requesterId);
        return incomes.findById(new IncomeId(incomeId))
                .filter(income -> income.belongsTo(household))
                .orElseThrow(IncomeNotFoundException::new);
    }
}
