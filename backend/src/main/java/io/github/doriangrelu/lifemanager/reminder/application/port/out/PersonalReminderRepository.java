package io.github.doriangrelu.lifemanager.reminder.application.port.out;

import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.PersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.PersonalReminderId;

import java.util.List;
import java.util.Optional;

/** Stockage des rappels saisis par les membres. */
public interface PersonalReminderRepository {

    Optional<PersonalReminder> findById(PersonalReminderId id);

    /** Rappels du foyer, du plus proche au plus lointain. */
    List<PersonalReminder> findAllByHousehold(HouseholdId householdId);

    /** Crée le rappel ou enregistre ses modifications. */
    void save(PersonalReminder reminder);

    void delete(PersonalReminderId id);
}
