package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Corps de {@code PUT /api/households/{householdId}/owner} : le membre qui devient propriétaire. */
@Serdeable
public record NewOwnerRequest(@NotNull UUID memberId) {
}
