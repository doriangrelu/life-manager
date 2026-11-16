package io.github.doriangrelu.lifemanager.reminder.application.port.out;

import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.Deadline;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.SourceId;

import java.util.List;
/** Stockage des échéances suivies. */
public interface DeadlineRepository {

    /** Échéances du foyer, de la plus ancienne à la plus lointaine. */
    List<Deadline> findAllByHousehold(HouseholdId householdId);

    /** Remplace toutes les échéances d'un objet d'origine par celles données (éventuellement aucune). */
    void replaceForSource(SourceId sourceId, List<Deadline> deadlines);
}
