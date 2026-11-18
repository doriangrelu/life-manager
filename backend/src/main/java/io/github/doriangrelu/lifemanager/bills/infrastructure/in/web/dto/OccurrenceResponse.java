package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.bills.application.port.in.result.OccurrenceView;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/** Une échéance ; statut {@code UPCOMING}, {@code TO_CONFIRM}, {@code PAID} ou {@code SKIPPED}. */
@Serdeable
public record OccurrenceResponse(UUID id, UUID chargeId, LocalDate dueOn, long expectedCents, String status,
                                 @Nullable Long paidCents, @Nullable LocalDate paidOn, int invoiceCount) {

    public static OccurrenceResponse from(OccurrenceView view) {
        return new OccurrenceResponse(view.id(), view.chargeId(), view.dueOn(), view.expectedCents(), view.status(),
                view.paidCents(), view.paidOn(), view.invoiceCount());
    }
}
