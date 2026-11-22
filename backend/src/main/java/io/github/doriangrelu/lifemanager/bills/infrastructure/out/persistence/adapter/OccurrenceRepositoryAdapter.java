package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.bills.application.port.out.OccurrenceRepository;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.ChargeOccurrence;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.mapper.OccurrenceMapper;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.repository.ChargeOccurrenceJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente le stockage des échéances avec PostgreSQL (table charge_occurrence). */
@Singleton
class OccurrenceRepositoryAdapter implements OccurrenceRepository {

    private final ChargeOccurrenceJdbcRepository table;

    OccurrenceRepositoryAdapter(ChargeOccurrenceJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<ChargeOccurrence> findById(OccurrenceId id) {
        return table.findById(id.value()).map(OccurrenceMapper::toDomain);
    }

    @Override
    public List<ChargeOccurrence> findAllByCharge(ChargeId chargeId) {
        return table.findAllByCharge(chargeId.value()).stream().map(OccurrenceMapper::toDomain).toList();
    }

    @Override
    public List<ChargeOccurrence> findAllByHousehold(HouseholdId householdId) {
        return table.findAllByHousehold(householdId.value()).stream().map(OccurrenceMapper::toDomain).toList();
    }

    @Override
    public List<ChargeOccurrence> findOpenByHousehold(HouseholdId householdId) {
        return table.findOpenByHousehold(householdId.value()).stream().map(OccurrenceMapper::toDomain).toList();
    }

    @Override
    public void save(ChargeOccurrence occurrence) {
        var entity = OccurrenceMapper.toEntity(occurrence);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void deleteUpcomingOfCharge(ChargeId chargeId) {
        table.deleteUpcoming(chargeId.value());
    }
}
