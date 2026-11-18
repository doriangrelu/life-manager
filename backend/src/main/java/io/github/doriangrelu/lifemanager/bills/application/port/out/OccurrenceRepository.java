package io.github.doriangrelu.lifemanager.bills.application.port.out;

import io.github.doriangrelu.lifemanager.bills.domain.aggregate.ChargeOccurrence;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;

import java.util.List;
import java.util.Optional;

/** Stockage des échéances. */
public interface OccurrenceRepository {

    Optional<ChargeOccurrence> findById(OccurrenceId id);

    /** Échéances d'une charge, de la plus récente à la plus ancienne. */
    List<ChargeOccurrence> findAllByCharge(ChargeId chargeId);

    /** Échéances encore ouvertes (à venir ou à confirmer) de toutes les charges du foyer. */
    List<ChargeOccurrence> findOpenByHousehold(HouseholdId householdId);

    void save(ChargeOccurrence occurrence);

    /** Supprime les échéances à venir d'une charge (recalculées après une modification ou une résiliation). */
    void deleteUpcomingOfCharge(ChargeId chargeId);
}
