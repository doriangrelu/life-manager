package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.reminder.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.repository.HouseholdMemberJdbcRepository;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Implémente la copie locale des membres de foyer avec PostgreSQL (table household_member). */
@Singleton
class HouseholdMembersAdapter implements HouseholdMembers {

    private final HouseholdMemberJdbcRepository table;

    HouseholdMembersAdapter(HouseholdMemberJdbcRepository table) {
        this.table = table;
    }

    @Override
    public boolean isMember(HouseholdId householdId, MemberId memberId) {
        return table.existsByHouseholdIdAndMemberId(householdId.value(), memberId.value());
    }

    @Override
    public void add(HouseholdId householdId, MemberId memberId) {
        table.insertIfAbsent(UUID.randomUUID(), householdId.value(), memberId.value());
    }
}
