package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.household.application.port.in.ChangeHouseholdSettings;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.HouseholdSettingsCommand;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.HouseholdSettingsRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.util.UUID;

/** Réglages du foyer (propriétaire) : suivi des rentrées. */
@Controller("/api/households/{householdId}/settings")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class HouseholdSettingsController {

    private final ChangeHouseholdSettings changeSettings;

    HouseholdSettingsController(ChangeHouseholdSettings changeSettings) {
        this.changeSettings = changeSettings;
    }

    @Put
    HttpResponse<Void> change(Authentication user, UUID householdId, @Body HouseholdSettingsRequest request) {
        changeSettings.change(new HouseholdSettingsCommand(user.getName(), householdId, request.incomeTracking()));
        return HttpResponse.noContent();
    }
}
