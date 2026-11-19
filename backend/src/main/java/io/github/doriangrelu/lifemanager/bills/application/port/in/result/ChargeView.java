package io.github.doriangrelu.lifemanager.bills.application.port.in.result;

import io.github.doriangrelu.lifemanager.bills.domain.aggregate.RecurringCharge;
import io.micronaut.core.annotation.Nullable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une charge vue hors du domaine.
 *
 * @param next prochaine échéance ouverte (à confirmer ou à venir), {@code null} s'il n'y en a pas
 * @param lastDueOn dernière échéance, {@code null} pour une charge sans fin
 * @param loanInsuranceCents assurance d'un crédit (0 = aucune), {@code null} si ce n'est pas un crédit
 */
public record ChargeView(UUID id, String name, long amountCents, String amountKind, String frequency,
                         LocalDate firstDueOn, UUID budgetCategoryId, UUID payerId, @Nullable UUID beneficiaryId,
                         boolean active, @Nullable OccurrenceView next, String direction,
                         @Nullable LocalDate lastDueOn, @Nullable Long loanInsuranceCents) {

    public static ChargeView from(RecurringCharge charge, OccurrenceView next) {
        var details = charge.details();
        var beneficiary = details.beneficiary();
        return new ChargeView(charge.id().value(), details.name().value(), details.amount().cents(),
                details.kind().name(), charge.schedule().frequency().name(), charge.schedule().firstDueOn(),
                details.budgetCategoryId(), details.payer().value(),
                beneficiary == null ? null : beneficiary.value(), charge.active(), next,
                details.direction().name(), charge.schedule().lastDueOn(),
                details.optionalLoan().map(loan -> loan.insurance().cents()).orElse(null));
    }
}
