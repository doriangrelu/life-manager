package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdCreatedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberJoinedV1;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.CreateDefaultShoppingLists;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.RegisterHouseholdMember;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.CreateDefaultShoppingListsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.RegisterHouseholdMemberCommand;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/**
 * Écoute les événements publics du domaine foyer : les courses savent ainsi qui est membre de quel foyer,
 * et préparent les sous-listes par défaut de chaque nouveau foyer.
 */
@Singleton
class HouseholdEventsListener {

    private final RegisterHouseholdMember registerHouseholdMember;
    private final CreateDefaultShoppingLists createDefaultShoppingLists;

    HouseholdEventsListener(RegisterHouseholdMember registerHouseholdMember,
                            CreateDefaultShoppingLists createDefaultShoppingLists) {
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
}
