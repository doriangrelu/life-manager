package io.github.doriangrelu.lifemanager.bills.application.port.out;

import io.github.doriangrelu.lifemanager.bills.domain.aggregate.RecurringCharge;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;

import java.util.List;
import java.util.Optional;

/** Stockage des charges. */
public interface ChargeRepository {

    Optional<RecurringCharge> findById(ChargeId id);

    List<RecurringCharge> findAllByHousehold(HouseholdId householdId);

    /** Charges actives de tous les foyers (pour la tâche quotidienne). */
    List<ChargeId> findAllActiveIds();

    void save(RecurringCharge charge);
}
