package io.github.doriangrelu.lifemanager.project.application.service;

import io.github.doriangrelu.lifemanager.project.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.project.domain.exception.ProjectHouseholdNotFoundException;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.MemberId;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Cloisonnement des projets : seuls les membres d'un foyer voient ses projets. */
@Singleton
public class ProjectHouseholdAccess {

    private final HouseholdMembers members;

    ProjectHouseholdAccess(HouseholdMembers members) {
        this.members = members;
    }

    /** @throws ProjectHouseholdNotFoundException si le demandeur n'est pas membre du foyer */
    public HouseholdId householdOfMember(UUID householdId, String requesterId) {
        var household = new HouseholdId(householdId);
        if (!members.isMember(household, MemberId.of(requesterId))) {
            throw new ProjectHouseholdNotFoundException();
        }
        return household;
    }
}
