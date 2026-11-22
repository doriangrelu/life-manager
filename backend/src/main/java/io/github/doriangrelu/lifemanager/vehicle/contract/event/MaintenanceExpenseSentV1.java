package io.github.doriangrelu.lifemanager.vehicle.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Événement public : le coût d'une opération d'entretien est ajouté au budget. Le budget en fait une dépense
 * pour tout le foyer, une seule par opération.
 * Contrat stable entre domaines (types simples, sérialisable en JSON).
 *
 * @param entryId          opération du carnet (sert de clé pour ne jamais créer deux dépenses)
 * @param label            libellé de la dépense (« Révision · Clio »)
 * @param budgetCategoryId catégorie de dépense choisie
 * @param payerId          membre qui a payé
 */
@Serdeable
public record MaintenanceExpenseSentV1(UUID entryId, UUID householdId, String label, long amountCents,
                                       LocalDate performedOn, UUID budgetCategoryId, UUID payerId,
                                       Instant occurredAt) {
}
