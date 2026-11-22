package io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.project.application.port.out.HouseholdData;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.repository.HouseholdDataJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

/** Effacements par foyer avec PostgreSQL (schéma project). */
@Singleton
class HouseholdDataAdapter implements HouseholdData {

    private final HouseholdDataJdbcRepository table;

    HouseholdDataAdapter(HouseholdDataJdbcRepository table) {
        this.table = table;
    }

    @Override
    public void forgetMember(HouseholdId householdId, MemberId memberId) {
        table.unassign(householdId.value(), memberId.value());
        table.deleteMember(householdId.value(), memberId.value());
    }

    @Override
    public List<UUID> erase(HouseholdId householdId) {
        var id = householdId.value();
        table.eraseAuthorship(id);
        table.eraseTasks(id);
        table.eraseProjects(id);
        table.deleteMembers(id);
        return List.of();
    }
}
