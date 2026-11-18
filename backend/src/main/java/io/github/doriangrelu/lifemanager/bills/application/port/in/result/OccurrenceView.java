package io.github.doriangrelu.lifemanager.bills.application.port.in.result;

import io.github.doriangrelu.lifemanager.bills.domain.aggregate.ChargeOccurrence;
import io.micronaut.core.annotation.Nullable;

import java.time.LocalDate;
import java.util.UUID;

/** Une échéance vue hors du domaine. Montants en centimes ; {@code paidCents} et {@code paidOn} si payée. */
public record OccurrenceView(UUID id, UUID chargeId, LocalDate dueOn, long expectedCents, String status,
                             @Nullable Long paidCents, @Nullable LocalDate paidOn, int invoiceCount) {

    public static OccurrenceView from(ChargeOccurrence occurrence, int invoiceCount) {
        var settlement = occurrence.settlement();
        var paid = settlement.paid();
        return new OccurrenceView(occurrence.id().value(), occurrence.chargeId().value(), occurrence.due().dueOn(),
                occurrence.due().expected().cents(), settlement.status().name(),
                paid == null ? null : paid.cents(), settlement.paidOn(), invoiceCount);
    }
}
