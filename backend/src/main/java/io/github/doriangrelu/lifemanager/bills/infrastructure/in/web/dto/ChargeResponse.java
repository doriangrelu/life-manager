package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ChargeView;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une charge avec sa prochaine échéance ouverte ({@code null} s'il n'y en a pas).
 * {@code loan} présent = crédit ; {@code lastDueOn} = dernière échéance, si la charge a une fin.
 */
@Serdeable
public record ChargeResponse(UUID id, String name, long amountCents, String amountKind, String frequency,
                             LocalDate firstDueOn, UUID budgetCategoryId, UUID payerId, @Nullable UUID beneficiaryId,
                             boolean active, @Nullable OccurrenceResponse next, String direction,
                             @Nullable LocalDate lastDueOn, @Nullable LoanRequest loan) {

    public static ChargeResponse from(ChargeView view) {
        return new ChargeResponse(view.id(), view.name(), view.amountCents(), view.amountKind(), view.frequency(),
                view.firstDueOn(), view.budgetCategoryId(), view.payerId(), view.beneficiaryId(), view.active(),
                view.next() == null ? null : OccurrenceResponse.from(view.next()), view.direction(),
                view.lastDueOn(),
                view.loanInsuranceCents() == null ? null : new LoanRequest(view.loanInsuranceCents()));
    }
}
