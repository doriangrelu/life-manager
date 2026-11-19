package io.github.doriangrelu.lifemanager.household.application.port.out;

import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;

import java.util.List;
import java.util.Optional;

/** Stockage des foyers et de leurs membres. */
public interface HouseholdRepository {

    Optional<Household> findById(HouseholdId id);

    /** Foyers dont l'utilisateur est membre, du plus ancien au plus récent. */
    List<Household> findAllByMember(MemberId member);

    /** Tous les foyers, avec leurs membres. */
    List<Household> findAll();

    /** Nombre de foyers dont l'utilisateur est membre. */
    int countByMember(MemberId member);

    /** Crée le foyer ou enregistre ses changements (nom, propriétaire, membres arrivés ou partis). */
    void save(Household household);

    /** Efface le foyer et ses appartenances (les invitations sont effacées à part, avant). */
    void delete(Household household);
}
