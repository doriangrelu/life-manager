package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.reminder.application.port.out.DeadlineRepository;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.Deadline;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.SourceId;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.mapper.DeadlineMapper;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.repository.DeadlineJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;

/** Implémente le stockage des échéances suivies avec PostgreSQL (table deadline). */
@Singleton
class DeadlineRepositoryAdapter implements DeadlineRepository {

    private final DeadlineJdbcRepository table;

    DeadlineRepositoryAdapter(DeadlineJdbcRepository table) {
        this.table = table;
    }

    @Override
    public List<Deadline> findAllByHousehold(HouseholdId householdId) {
        return table.findAllByHousehold(householdId.value()).stream().map(DeadlineMapper::toDomain).toList();
    }

    @Override
    public void replaceForSource(SourceId sourceId, List<Deadline> deadlines) {
        table.deleteBySource(sourceId.value());
        deadlines.stream().map(DeadlineMapper::toEntity).forEach(table::save);
    }
}
