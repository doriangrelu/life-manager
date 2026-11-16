package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/** Corps de {@code POST /api/households}. */
@Serdeable
public record CreateHouseholdRequest(@NotBlank @Size(max = 100) String name) {
}
