package io.github.doriangrelu.lifemanager.bills.application.port.out;

import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.MemberId;

/** Copie locale des membres de chaque foyer, alimentée par les événements du domaine foyer. */
public interface HouseholdMembers {

    boolean isMember(HouseholdId householdId, MemberId memberId);

    /** Ajoute l'appartenance si elle est inconnue (un même événement peut arriver deux fois). */
    void add(HouseholdId householdId, MemberId memberId);
}
