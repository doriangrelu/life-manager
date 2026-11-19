package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdCreatedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdDeletedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberJoinedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberLeftV1;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.CreateDefaultShoppingLists;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.RegisterHouseholdMember;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.CreateDefaultShoppingListsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.RegisterHouseholdMemberCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.ForgetHousehold;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.ForgetHouseholdMember;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ForgetHouseholdCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ForgetHouseholdMemberCommand;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/**
 * Écoute les événements publics du domaine foyer : les courses savent ainsi qui est membre de quel foyer,
 * et préparent les sous-listes par défaut de chaque nouveau foyer.
 */
@Singleton
class HouseholdEventsListener {

    private final ForgetHouseholdMember forgetMember;
    private final ForgetHousehold forgetHousehold;
    private final RegisterHouseholdMember registerHouseholdMember;
    private final CreateDefaultShoppingLists createDefaultShoppingLists;

    HouseholdEventsListener(RegisterHouseholdMember registerHouseholdMember,
                            CreateDefaultShoppingLists createDefaultShoppingLists,
                            ForgetHouseholdMember forgetMember, ForgetHousehold forgetHousehold) {
        this.forgetMember = forgetMember;
        this.forgetHousehold = forgetHousehold;
        this.registerHouseholdMember = registerHouseholdMember;
        this.createDefaultShoppingLists = createDefaultShoppingLists;
    }

    @EventListener
    void on(HouseholdCreatedV1 event) {
        registerHouseholdMember.register(new RegisterHouseholdMemberCommand(event.householdId(), event.founderId()));
        createDefaultShoppingLists.create(new CreateDefaultShoppingListsCommand(event.householdId()));
    }

    @EventListener
    void on(MemberJoinedV1 event) {
        registerHouseholdMember.register(new RegisterHouseholdMemberCommand(event.householdId(), event.memberId()));
    }

    /** Membre parti ou retiré : plus d'accès au foyer dans ce domaine. */
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
