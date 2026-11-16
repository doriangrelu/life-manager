package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
/** Corps de {@code POST /api/households/join}. */
@Serdeable
public record JoinHouseholdRequest(@NotBlank String code) {
}
