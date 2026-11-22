package io.github.doriangrelu.lifemanager.project.contract.event;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement public : un projet a été créé ou modifié (pour le budget).
 *
 * @param status         ACTIVE, DONE ou ABANDONED
 * @param countsInBudget vrai si ses dépenses comptent dans le budget du mois
 */
@Serdeable
public record ProjectChangedV1(UUID projectId, UUID householdId, String name, String color, String status,
                               boolean countsInBudget, @Nullable Long plannedCents, Instant occurredAt) {
}
