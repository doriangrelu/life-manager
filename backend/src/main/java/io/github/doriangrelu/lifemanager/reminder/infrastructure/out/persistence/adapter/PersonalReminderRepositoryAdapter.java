package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.reminder.application.port.out.PersonalReminderRepository;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.PersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.NoticePeriod;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.PersonalReminderDetails;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.PersonalReminderId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Recurrence;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderNote;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderTitle;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity.PersonalReminderEntity;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.repository.PersonalReminderJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente le stockage des rappels saisis avec PostgreSQL (table personal_reminder). */
@Singleton
class PersonalReminderRepositoryAdapter implements PersonalReminderRepository {

    private final PersonalReminderJdbcRepository table;

    PersonalReminderRepositoryAdapter(PersonalReminderJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<PersonalReminder> findById(PersonalReminderId id) {
        return table.findById(id.value()).map(PersonalReminderRepositoryAdapter::toDomain);
    }

    @Override
    public List<PersonalReminder> findAllByHousehold(HouseholdId householdId) {
        return table.findAllByHousehold(householdId.value()).stream()
                .map(PersonalReminderRepositoryAdapter::toDomain).toList();
    }

    @Override
    public void save(PersonalReminder reminder) {
        var entity = toEntity(reminder);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void delete(PersonalReminderId id) {
        table.deleteById(id.value());
    }

    private static PersonalReminderEntity toEntity(PersonalReminder reminder) {
        var details = reminder.details();
        return new PersonalReminderEntity(reminder.id().value(), reminder.householdId().value(),
                details.title().value(), details.note() == null ? null : details.note().value(), details.dueOn(),
                reminder.anchorOn(), details.recurrence().name(), details.notice().name());
    }

    private static PersonalReminder toDomain(PersonalReminderEntity entity) {
        var details = new PersonalReminderDetails(new ReminderTitle(entity.title()),
                ReminderNote.ofNullable(entity.note()), entity.dueOn(), Recurrence.of(entity.recurrence()),
                NoticePeriod.of(entity.notice()));
        return PersonalReminder.restore(new PersonalReminderId(entity.id()), new HouseholdId(entity.householdId()),
                details, entity.anchorOn());
    }
}
