package io.github.doriangrelu.lifemanager.household.application.port.out;

import io.github.doriangrelu.lifemanager.household.domain.aggregate.Invitation;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationCode;

import java.util.Optional;

/** Stockage des invitations. */
public interface InvitationRepository {

    Optional<Invitation> findByCode(InvitationCode code);

    /** Invitation encore en attente du foyer, s'il y en a une (au plus une). */
    Optional<Invitation> findPendingByHousehold(HouseholdId householdId);

    /**
     * Crée l'invitation ou enregistre son changement de statut.
     * Si une autre requête l'a utilisée entre-temps, lève {@code InvalidInvitationException}.
     */
    void save(Invitation invitation);
}
