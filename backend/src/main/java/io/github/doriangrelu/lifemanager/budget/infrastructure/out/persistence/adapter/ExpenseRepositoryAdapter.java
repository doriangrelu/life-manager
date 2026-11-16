package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.out.ExpenseRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Expense;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.mapper.ExpenseMapper;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.ExpenseJdbcRepository;
import jakarta.inject.Singleton;

import java.util.Optional;

/** Implémente le stockage des dépenses avec PostgreSQL (table expense). */
@Singleton
class ExpenseRepositoryAdapter implements ExpenseRepository {

    private final ExpenseJdbcRepository table;

    ExpenseRepositoryAdapter(ExpenseJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<Expense> findById(ExpenseId id) {
        return table.findById(id.value()).map(ExpenseMapper::toDomain);
    }

    @Override
    public void save(Expense expense) {
        var entity = ExpenseMapper.toEntity(expense);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void delete(Expense expense) {
        table.deleteById(expense.id().value());
    }
}
