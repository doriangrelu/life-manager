package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
/** Copie locale des membres de chaque foyer (alimentée par les événements du domaine foyer). */
public interface HouseholdMembers {

    boolean isMember(HouseholdId householdId, MemberId memberId);

    /** Vrai si le foyer a encore au moins un membre ici (faux une fois le foyer supprimé). */
    boolean isKnown(HouseholdId householdId);

    /** Enregistre l'appartenance ; sans effet si elle est déjà connue. */
    void add(HouseholdId householdId, MemberId memberId);
}
