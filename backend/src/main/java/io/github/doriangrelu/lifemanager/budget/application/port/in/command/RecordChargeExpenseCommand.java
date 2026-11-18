package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Échéance de charge payée, reçue du domaine des charges.
 *
 * @param beneficiaryId membre pour qui, {@code null} pour tout le foyer
 */
public record RecordChargeExpenseCommand(UUID occurrenceId, UUID householdId, String chargeName, long amountCents,
                                         LocalDate paidOn, UUID budgetCategoryId, UUID payerId, UUID beneficiaryId) {
}
