package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.bills.application.port.out.ChargeRepository;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.RecurringCharge;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.mapper.ChargeMapper;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.repository.RecurringChargeJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente le stockage des charges avec PostgreSQL (table recurring_charge). */
@Singleton
class ChargeRepositoryAdapter implements ChargeRepository {

    private final RecurringChargeJdbcRepository table;

    ChargeRepositoryAdapter(RecurringChargeJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<RecurringCharge> findById(ChargeId id) {
        return table.findById(id.value()).map(ChargeMapper::toDomain);
    }

    @Override
    public List<RecurringCharge> findAllByHousehold(HouseholdId householdId) {
        return table.findAllByHousehold(householdId.value()).stream().map(ChargeMapper::toDomain).toList();
    }

    @Override
    public List<ChargeId> findAllActiveIds() {
        return table.findActiveIds().stream().map(ChargeId::new).toList();
    }

    @Override
    public void save(RecurringCharge charge) {
        var entity = ChargeMapper.toEntity(charge);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }
}
