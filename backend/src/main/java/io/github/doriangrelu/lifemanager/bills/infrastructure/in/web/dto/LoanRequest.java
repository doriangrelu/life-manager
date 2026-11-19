package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Présent dans la saisie d'une charge : elle est un crédit.
 *
 * @param insuranceCents mensualité d'assurance en centimes, comprise dans le montant de la charge (0 = aucune)
 */
@Serdeable
public record LoanRequest(@PositiveOrZero @Max(10_000_000) long insuranceCents) {
}
