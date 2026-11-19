package io.github.doriangrelu.lifemanager.bills.application.port.in.result;

import io.micronaut.core.annotation.Nullable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une échéance encore attendue dans la période, à son montant prévu (centimes).
 *
 * @param occurrenceId échéance déjà créée, ou {@code null} si elle n'est que calculée depuis le calendrier
 * @param status       {@code UPCOMING}, {@code TO_CONFIRM} ou {@code PLANNED} (calculée)
 * @param direction    {@code EXPENSE} ou {@code INCOME}
 */
public record ForecastItem(@Nullable UUID occurrenceId, UUID chargeId, String name, LocalDate dueOn,
                           long expectedCents, String status, String direction, UUID budgetCategoryId) {
}
