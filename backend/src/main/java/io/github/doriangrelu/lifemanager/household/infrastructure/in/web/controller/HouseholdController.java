package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.household.application.port.in.CreateHousehold;
import io.github.doriangrelu.lifemanager.household.application.port.in.GetHousehold;
import io.github.doriangrelu.lifemanager.household.application.port.in.ListMyHouseholds;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.CreateHouseholdCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.query.GetHouseholdQuery;
import io.github.doriangrelu.lifemanager.household.application.port.in.query.ListMyHouseholdsQuery;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.CreateHouseholdRequest;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.HouseholdDetailsResponse;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.HouseholdSummaryResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

/** Foyers de l'utilisateur connecté : liste, création, détail. */
@Controller("/api/households")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class HouseholdController {

    private final ListMyHouseholds listMyHouseholds;
    private final CreateHousehold createHousehold;
    private final GetHousehold getHousehold;

    HouseholdController(ListMyHouseholds listMyHouseholds, CreateHousehold createHousehold,
                        GetHousehold getHousehold) {
        this.listMyHouseholds = listMyHouseholds;
        this.createHousehold = createHousehold;
        this.getHousehold = getHousehold;
    }

    @Get
    List<HouseholdSummaryResponse> list(Authentication user) {
        return listMyHouseholds.list(new ListMyHouseholdsQuery(user.getName())).stream()
                .map(HouseholdSummaryResponse::from)
                .toList();
    }

    @Post
    HttpResponse<HouseholdSummaryResponse> create(Authentication user, @Body @Valid CreateHouseholdRequest request) {
        var created = createHousehold.create(new CreateHouseholdCommand(user.getName(), request.name()));
        return HttpResponse.created(HouseholdSummaryResponse.from(created));
    }

    @Get("/{householdId}")
    HouseholdDetailsResponse get(Authentication user, UUID householdId) {
        return HouseholdDetailsResponse.from(getHousehold.get(new GetHouseholdQuery(user.getName(), householdId)));
    }
}
