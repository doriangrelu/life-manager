package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corps de {@code DELETE /api/households/{householdId}} : le nom du foyer retapé, pour confirmer. */
@Serdeable
public record DeleteHouseholdRequest(@NotBlank @Size(max = 100) String confirmName) {
}
