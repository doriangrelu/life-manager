package io.github.doriangrelu.lifemanager.project.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.project.application.port.in.ForgetHousehold;
import io.github.doriangrelu.lifemanager.project.application.port.in.ForgetHouseholdMember;
import io.github.doriangrelu.lifemanager.project.application.port.in.RegisterHouseholdMember;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.ForgetHouseholdCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.ForgetHouseholdMemberCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.RegisterHouseholdMemberCommand;
import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdCreatedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdDeletedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberJoinedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberLeftV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MembershipKnownV1;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/** Écoute les événements publics du domaine foyer : les projets savent ainsi qui est membre de quel foyer. */
@Singleton
class HouseholdEventsListener {

    private final ForgetHouseholdMember forgetMember;
    private final ForgetHousehold forgetHousehold;
    private final RegisterHouseholdMember registerHouseholdMember;

    HouseholdEventsListener(RegisterHouseholdMember registerHouseholdMember,
                            ForgetHouseholdMember forgetMember, ForgetHousehold forgetHousehold) {
        this.forgetMember = forgetMember;
        this.forgetHousehold = forgetHousehold;
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

    /** Rappel au démarrage : rattrape les foyers créés avant ce domaine. Sans effet si le membre est connu. */
    @EventListener
    void on(MembershipKnownV1 event) {
        registerHouseholdMember.register(new RegisterHouseholdMemberCommand(event.householdId(), event.memberId()));
    }

    /** Membre parti ou retiré : plus d'accès au foyer, ses tâches n'ont plus de responsable. */
    @EventListener
    void on(MemberLeftV1 event) {
        forgetMember.forget(new ForgetHouseholdMemberCommand(event.householdId(), event.memberId(), event.ownerId()));
    }

    /** Foyer supprimé : toutes ses données de ce domaine sont effacées. */
    @EventListener
    void on(HouseholdDeletedV1 event) {
        forgetHousehold.forget(new ForgetHouseholdCommand(event.householdId()));
    }
}
