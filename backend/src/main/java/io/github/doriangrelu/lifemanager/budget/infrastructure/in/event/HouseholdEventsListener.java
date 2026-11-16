package io.github.doriangrelu.lifemanager.budget.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.budget.application.port.in.CreateDefaultCategories;
import io.github.doriangrelu.lifemanager.budget.application.port.in.RegisterHouseholdMember;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CreateDefaultCategoriesCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RegisterHouseholdMemberCommand;
import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdCreatedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberJoinedV1;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/**
 * Écoute les événements publics du domaine foyer : le budget sait ainsi qui est membre de quel foyer,
 * et prépare les catégories par défaut de chaque nouveau foyer.
 */
@Singleton
class HouseholdEventsListener {

    private final RegisterHouseholdMember registerHouseholdMember;
    private final CreateDefaultCategories createDefaultCategories;

    HouseholdEventsListener(RegisterHouseholdMember registerHouseholdMember,
                            CreateDefaultCategories createDefaultCategories) {
        this.registerHouseholdMember = registerHouseholdMember;
        this.createDefaultCategories = createDefaultCategories;
    }

    @EventListener
    void on(HouseholdCreatedV1 event) {
        registerHouseholdMember.register(new RegisterHouseholdMemberCommand(event.householdId(), event.founderId()));
        createDefaultCategories.create(new CreateDefaultCategoriesCommand(event.householdId()));
    }

    @EventListener
    void on(MemberJoinedV1 event) {
        registerHouseholdMember.register(new RegisterHouseholdMemberCommand(event.householdId(), event.memberId()));
    }
}
