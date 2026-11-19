package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.reminder.application.port.out.ReminderDoneRepository;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.ReminderDone;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Completion;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderDoneId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderTitle;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity.ReminderDoneEntity;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.repository.ReminderDoneJdbcRepository;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Implémente le stockage de l'historique avec PostgreSQL (table reminder_done). */
@Singleton
class ReminderDoneRepositoryAdapter implements ReminderDoneRepository {

    private final ReminderDoneJdbcRepository table;

    ReminderDoneRepositoryAdapter(ReminderDoneJdbcRepository table) {
        this.table = table;
    }

    @Override
    public void save(ReminderDone entry) {
        // Une ligne d'historique ne change jamais : toujours une insertion.
        table.save(toEntity(entry));
    }

    @Override
    public Optional<ReminderDone> findById(ReminderDoneId id) {
        return table.findById(id.value()).map(ReminderDoneRepositoryAdapter::toDomain);
    }

    @Override
    public List<ReminderDone> findPage(HouseholdId householdId, int offset, int limit) {
        return table.findPage(householdId.value(), limit, offset).stream()
                .map(ReminderDoneRepositoryAdapter::toDomain).toList();
    }

    @Override
    public void delete(ReminderDoneId id) {
        table.deleteById(id.value());
    }

    @Override
    public void deleteDoneBefore(Instant moment) {
        table.deleteDoneBefore(moment);
    }

    private static ReminderDoneEntity toEntity(ReminderDone entry) {
        var done = entry.completion();
        return new ReminderDoneEntity(entry.id().value(), entry.householdId().value(), done.title().value(),
                done.dueOn(), done.doneAt(), done.doneBy().value());
    }

    private static ReminderDone toDomain(ReminderDoneEntity entity) {
        var completion = new Completion(new ReminderTitle(entity.title()), entity.dueOn(), entity.doneAt(),
                new MemberId(entity.doneBy()));
        return ReminderDone.restore(new ReminderDoneId(entity.id()), new HouseholdId(entity.householdId()),
                completion);
    }
}
