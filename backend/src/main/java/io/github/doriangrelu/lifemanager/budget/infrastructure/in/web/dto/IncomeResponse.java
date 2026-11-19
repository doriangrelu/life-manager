package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.IncomeView;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une rentrée. Montant en centimes d'euro.
 *
 * @param receivedBy membre qui la reçoit, ou {@code null} pour le foyer entier
 */
@Serdeable
public record IncomeResponse(UUID id, long amountCents, UUID categoryId, LocalDate receivedOn,
                             @Nullable UUID receivedBy, @Nullable String label) {

    public static IncomeResponse from(IncomeView income) {
        return new IncomeResponse(income.id(), income.amountCents(), income.categoryId(), income.receivedOn(),
                income.receivedBy(), income.label());
    }
}
