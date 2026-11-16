package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.Deadline;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineDetails;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineKind;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.SourceId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Subject;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity.DeadlineEntity;

/** Conversion entre l'agrégat {@link Deadline} et sa ligne en base. */
public final class DeadlineMapper {

    private DeadlineMapper() {
    }

    public static DeadlineEntity toEntity(Deadline deadline) {
        var details = deadline.details();
        return new DeadlineEntity(deadline.id().value(), deadline.householdId().value(),
                deadline.sourceId().value(), details.kind().name(), details.subject().value(), details.dueOn());
    }

    public static Deadline toDomain(DeadlineEntity entity) {
        var details = new DeadlineDetails(DeadlineKind.of(entity.kind()), new Subject(entity.subject()),
                entity.dueOn());
        return Deadline.restore(new DeadlineId(entity.id()), new HouseholdId(entity.householdId()),
                new SourceId(entity.sourceId()), details);
    }
}
