package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.out.ChargeIncomeLinks;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.ChargeIncomeJdbcRepository;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Implémente la correspondance échéance → rentrée avec PostgreSQL (table charge_income). */
@Singleton
class ChargeIncomeLinksAdapter implements ChargeIncomeLinks {

    private final ChargeIncomeJdbcRepository table;

    ChargeIncomeLinksAdapter(ChargeIncomeJdbcRepository table) {
        this.table = table;
    }

    @Override
    public boolean claim(UUID occurrenceId, IncomeId incomeId) {
        return table.insertIfAbsent(occurrenceId, incomeId.value()) == 1;
    }
}
