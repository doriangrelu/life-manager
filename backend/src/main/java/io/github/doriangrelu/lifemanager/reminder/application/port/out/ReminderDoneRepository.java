package io.github.doriangrelu.lifemanager.reminder.application.port.out;

import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.ReminderDone;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderDoneId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Stockage de l'historique des rappels faits. */
public interface ReminderDoneRepository {

    void save(ReminderDone entry);

    Optional<ReminderDone> findById(ReminderDoneId id);

    /** Une page de l'historique du foyer, de la plus récente à la plus ancienne. */
    List<ReminderDone> findPage(HouseholdId householdId, int offset, int limit);

    void delete(ReminderDoneId id);

    /** Efface les lignes faites avant ce moment, tous foyers confondus. */
    void deleteDoneBefore(Instant moment);
}
