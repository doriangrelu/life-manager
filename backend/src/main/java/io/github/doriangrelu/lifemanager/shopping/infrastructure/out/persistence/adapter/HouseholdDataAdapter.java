package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.shopping.application.port.out.HouseholdData;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.repository.HouseholdDataJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

/** Effacements par foyer avec PostgreSQL (schéma shopping). */
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
        table.erase1(id);
        table.deleteMembers(id);
        return List.of();
    }
}
