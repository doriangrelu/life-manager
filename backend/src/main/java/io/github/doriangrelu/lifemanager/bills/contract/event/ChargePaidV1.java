package io.github.doriangrelu.lifemanager.bills.contract.event;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Événement public : une échéance de charge a été payée. Le budget en fait une dépense (une seule par échéance).
 * Contrat stable entre domaines (types simples, sérialisable en JSON).
 *
 * @param beneficiaryId membre pour qui, {@code null} pour tout le foyer
 */
@Serdeable
public record ChargePaidV1(UUID occurrenceId, UUID householdId, String chargeName, long amountCents, LocalDate paidOn,
                           UUID budgetCategoryId, UUID payerId, @Nullable UUID beneficiaryId, Instant occurredAt) {
}
