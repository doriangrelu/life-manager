package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.bills.domain.aggregate.ChargeOccurrence;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Amount;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Due;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceStatus;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Settlement;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity.ChargeOccurrenceEntity;

/** Conversion entre l'agrégat {@link ChargeOccurrence} et sa ligne en base. */
public final class OccurrenceMapper {

    private OccurrenceMapper() {
    }

    public static ChargeOccurrenceEntity toEntity(ChargeOccurrence occurrence) {
        var settlement = occurrence.settlement();
        var paid = settlement.paid();
        return new ChargeOccurrenceEntity(occurrence.id().value(), occurrence.chargeId().value(),
                occurrence.householdId().value(), occurrence.due().dueOn(), occurrence.due().expected().cents(),
                settlement.status().name(), paid == null ? null : paid.cents(), settlement.paidOn());
    }

    public static ChargeOccurrence toDomain(ChargeOccurrenceEntity entity) {
        var paid = entity.paidCents() == null ? null : Amount.ofCents(entity.paidCents());
        var settlement = new Settlement(OccurrenceStatus.valueOf(entity.status()), paid, entity.paidOn());
        return ChargeOccurrence.restore(new OccurrenceId(entity.id()), new ChargeId(entity.chargeId()),
                new HouseholdId(entity.householdId()), new Due(entity.dueOn(), Amount.ofCents(entity.expectedCents())),
                settlement);
    }
}
