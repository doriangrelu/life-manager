package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.out.IncomeRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Income;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.mapper.IncomeMapper;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.IncomeJdbcRepository;
import jakarta.inject.Singleton;

import java.util.Optional;

/** Implémente le stockage des rentrées avec PostgreSQL (table income). */
@Singleton
class IncomeRepositoryAdapter implements IncomeRepository {

    private final IncomeJdbcRepository table;

    IncomeRepositoryAdapter(IncomeJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<Income> findById(IncomeId id) {
        return table.findById(id.value()).map(IncomeMapper::toDomain);
    }

    @Override
    public void save(Income income) {
        var entity = IncomeMapper.toEntity(income);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void delete(Income income) {
        table.deleteById(income.id().value());
    }
}
