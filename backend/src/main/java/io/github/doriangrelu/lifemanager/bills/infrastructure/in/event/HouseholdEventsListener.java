package io.github.doriangrelu.lifemanager.bills.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.bills.application.port.in.RegisterHouseholdMember;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.RegisterHouseholdMemberCommand;
import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdCreatedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberJoinedV1;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/** Écoute les événements publics du domaine foyer : le domaine des charges sait ainsi qui est membre de quel foyer. */
@Singleton
class HouseholdEventsListener {

    private final RegisterHouseholdMember registerHouseholdMember;

    HouseholdEventsListener(RegisterHouseholdMember registerHouseholdMember) {
        this.registerHouseholdMember = registerHouseholdMember;
    }

    @EventListener
    void on(HouseholdCreatedV1 event) {
        registerHouseholdMember.register(new RegisterHouseholdMemberCommand(event.householdId(), event.founderId()));
    }

    @EventListener
    void on(MemberJoinedV1 event) {
        registerHouseholdMember.register(new RegisterHouseholdMemberCommand(event.householdId(), event.memberId()));
    }
}
