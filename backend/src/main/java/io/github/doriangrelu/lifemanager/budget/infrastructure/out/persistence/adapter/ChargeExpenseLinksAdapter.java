package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.out.ChargeExpenseLinks;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.ChargeExpenseJdbcRepository;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Implémente la correspondance échéance → dépense avec PostgreSQL (table charge_expense). */
@Singleton
class ChargeExpenseLinksAdapter implements ChargeExpenseLinks {

    private final ChargeExpenseJdbcRepository table;

    ChargeExpenseLinksAdapter(ChargeExpenseJdbcRepository table) {
        this.table = table;
    }

    @Override
    public boolean claim(UUID occurrenceId, ExpenseId expenseId) {
        return table.insertIfAbsent(occurrenceId, expenseId.value()) == 1;
    }
}
