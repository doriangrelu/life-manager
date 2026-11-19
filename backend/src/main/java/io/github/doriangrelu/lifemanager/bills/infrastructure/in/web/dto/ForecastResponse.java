package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ForecastItem;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une échéance encore attendue, à son montant prévu (centimes).
 *
 * @param occurrenceId {@code null} pour une échéance seulement calculée ({@code status} = {@code PLANNED})
 * @param direction    {@code EXPENSE} ou {@code INCOME}
 */
@Serdeable
public record ForecastResponse(@Nullable UUID occurrenceId, UUID chargeId, String name, LocalDate dueOn,
                               long expectedCents, String status, String direction, UUID budgetCategoryId) {

    public static ForecastResponse from(ForecastItem item) {
        return new ForecastResponse(item.occurrenceId(), item.chargeId(), item.name(), item.dueOn(),
                item.expectedCents(), item.status(), item.direction(), item.budgetCategoryId());
    }
}
