package io.github.doriangrelu.lifemanager.bills.application.port.in.command;

import io.micronaut.core.annotation.Nullable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Saisie d'une charge, telle que reçue (vérifiée par le domaine).
 *
 * @param amountKind    {@code FIXED} ou {@code VARIABLE}
 * @param frequency     {@code MONTHLY}, {@code BIMONTHLY}, {@code QUARTERLY} ou {@code YEARLY}
 * @param beneficiaryId membre pour qui, {@code null} pour tout le foyer
 * @param direction     {@code EXPENSE} (ou {@code null}) ou {@code INCOME}
 */
public record ChargeInput(String name, long amountCents, String amountKind, String frequency, LocalDate firstDueOn,
                          UUID budgetCategoryId, UUID payerId, @Nullable UUID beneficiaryId,
                          @Nullable String direction) {
}
