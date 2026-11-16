package io.github.doriangrelu.lifemanager.vehicle.application.port.out;

import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
/** Copie locale des membres de chaque foyer (alimentée par les événements du domaine foyer). */
public interface HouseholdMembers {

    boolean isMember(HouseholdId householdId, MemberId memberId);

    /** Enregistre l'appartenance ; sans effet si elle est déjà connue. */
    void add(HouseholdId householdId, MemberId memberId);
}
