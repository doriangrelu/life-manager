package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.bills.application.port.out.HouseholdData;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.repository.HouseholdDataJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

/** Effacements par foyer avec PostgreSQL (schéma bills). */
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
    public void reassignCharges(HouseholdId householdId, MemberId memberId, MemberId ownerId) {
        table.reassignPayer(householdId.value(), memberId.value(), ownerId.value());
        table.shareBeneficiary(householdId.value(), memberId.value());
    }

    @Override
    public List<UUID> erase(HouseholdId householdId) {
        var id = householdId.value();
        var keys = table.storageKeys(id);
        table.erase1(id);
        table.erase2(id);
        table.erase3(id);
        table.deleteMembers(id);
        return keys;
    }
}
