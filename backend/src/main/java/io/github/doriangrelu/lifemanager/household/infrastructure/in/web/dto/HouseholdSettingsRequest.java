package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;

/** Corps de {@code PUT /api/households/{householdId}/settings}. */
@Serdeable
public record HouseholdSettingsRequest(boolean incomeTracking) {
}
