package io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Ligne de la table {@code project.project}. */
@MappedEntity("project")
public record ProjectEntity(@Id UUID id, UUID householdId, String name, @Nullable String description, String color,
                            @Nullable Long plannedCents, @Nullable LocalDate startOn, @Nullable LocalDate targetOn,
                            String status, boolean countsInBudget, Instant createdAt) {
}
