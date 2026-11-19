package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.HouseholdData;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository.HouseholdDataJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

/** Effacements par foyer avec PostgreSQL (schéma vehicle). */
@Singleton
class HouseholdDataAdapter implements HouseholdData {

    private final HouseholdDataJdbcRepository table;

    HouseholdDataAdapter(HouseholdDataJdbcRepository table) {
        this.table = table;
    }

    @Override
    public void forgetMember(HouseholdId householdId, MemberId memberId) {
        table.deleteMember(householdId.value(), memberId.value());
    }

    @Override
    public List<UUID> erase(HouseholdId householdId) {
        var id = householdId.value();
        var keys = table.storageKeys(id);
        table.erase1(id);
        table.deleteMembers(id);
        return keys;
    }
}
