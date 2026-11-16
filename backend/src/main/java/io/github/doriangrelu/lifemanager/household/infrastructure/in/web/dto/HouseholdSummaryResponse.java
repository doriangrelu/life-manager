package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.household.application.port.in.result.HouseholdSummary;
import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;
/** Un foyer dans une liste. */
@Serdeable
public record HouseholdSummaryResponse(UUID id, String name) {

    public static HouseholdSummaryResponse from(HouseholdSummary summary) {
        return new HouseholdSummaryResponse(summary.id(), summary.name());
    }
}
