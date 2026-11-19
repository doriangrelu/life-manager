package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ChargeInput;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Corps d'ajout ou de modification d'une charge ; montant en centimes ; sens absent = dépense.
 *
 * @param lastDueOn dernière échéance (fin d'un crédit), absente pour une charge sans fin
 * @param loan      présent si la charge est un crédit : le montant est alors le total, assurance comprise
 */
@Serdeable
public record ChargeRequest(@NotBlank @Size(max = 60) String name,
                            long amountCents,
                            @NotBlank String amountKind,
                            @NotBlank String frequency,
                            @NotNull LocalDate firstDueOn,
                            @NotNull UUID budgetCategoryId,
                            @NotNull UUID payerId,
                            @Nullable UUID beneficiaryId,
                            @Nullable String direction,
                            @Nullable LocalDate lastDueOn,
                            @Nullable @Valid LoanRequest loan) {

    public ChargeInput toInput() {
        return new ChargeInput(name, amountCents, amountKind, frequency, firstDueOn, budgetCategoryId, payerId,
                beneficiaryId, direction, lastDueOn, loan == null ? null : loan.insuranceCents());
    }
}
