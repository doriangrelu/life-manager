package io.github.doriangrelu.lifemanager.shopping.application.port.out;

import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;

import java.util.List;
import java.util.UUID;

/** Données du domaine rattachées à un foyer : départ d'un membre, effacement du foyer. Tout est idempotent. */
public interface HouseholdData {

    /** Retire le membre de la copie locale des membres : il n'a plus accès au foyer dans ce domaine. */
    void forgetMember(HouseholdId householdId, MemberId memberId);

    /** Efface toutes les données du foyer ; renvoie les clés des fichiers à supprimer une fois la transaction validée. */
    List<UUID> erase(HouseholdId householdId);
}
