package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.out.HouseholdData;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.HouseholdDataJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

/** Effacements par foyer avec PostgreSQL (schéma budget). */
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
        table.eraseAuthorship(id);
        table.erase1(id);
        table.erase2(id);
        table.eraseIncomeLinks(id);
        table.eraseIncomes(id);
        table.erase3(id); // catégories en dernier : dépenses et rentrées y font référence
        table.deleteMembers(id);
        return List.of();
    }
}
