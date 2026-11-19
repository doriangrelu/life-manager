package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corps de {@code PUT /api/households/{householdId}} : nouveau nom (mêmes règles qu'à la création). */
@Serdeable
public record RenameHouseholdRequest(@NotBlank @Size(max = 100) String name) {
}
