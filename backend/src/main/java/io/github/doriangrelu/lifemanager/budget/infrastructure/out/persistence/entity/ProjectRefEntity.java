package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.util.UUID;

/** Ligne de la table {@code budget.project_ref} (copie locale des projets). */
@MappedEntity("project_ref")
public record ProjectRefEntity(@Id UUID projectId, UUID householdId, String name, String status,
                               boolean countsInBudget) {
}
