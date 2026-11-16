package io.github.doriangrelu.lifemanager.reminder.application.service;

import io.github.doriangrelu.lifemanager.reminder.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.reminder.domain.exception.ReminderHouseholdNotFoundException;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.MemberId;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Cloisonnement des rappels : seuls les membres d'un foyer voient ses rappels. */
@Singleton
public class ReminderAccess {

    private final HouseholdMembers members;

    ReminderAccess(HouseholdMembers members) {
        this.members = members;
    }

    /** @throws ReminderHouseholdNotFoundException si le demandeur n'est pas membre du foyer */
    public HouseholdId householdOfMember(UUID householdId, String requesterId) {
        var household = new HouseholdId(householdId);
        if (!members.isMember(household, MemberId.of(requesterId))) {
            throw new ReminderHouseholdNotFoundException();
        }
        return household;
    }
}
