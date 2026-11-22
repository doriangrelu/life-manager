package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.CorrectWearPart;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.DeleteWearPart;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.FitWearPart;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.ListWearParts;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartCommand;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.WearPartRequest;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.WearPartResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

/** Pièces d'usure d'un véhicule : liste, pose (remplace la pièce montée du même type), correction, suppression. */
@Controller("/api/households/{householdId}/vehicles/{vehicleId}/parts")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class WearPartController {

    private final ListWearParts listParts;
    private final FitWearPart fitPart;
    private final CorrectWearPart correctPart;
    private final DeleteWearPart deletePart;

    WearPartController(ListWearParts listParts, FitWearPart fitPart, CorrectWearPart correctPart,
                       DeleteWearPart deletePart) {
        this.listParts = listParts;
        this.fitPart = fitPart;
        this.correctPart = correctPart;
        this.deletePart = deletePart;
    }

    @Get
    List<WearPartResponse> list(Authentication user, UUID householdId, UUID vehicleId) {
        return listParts.list(new WearPartCommand(user.getName(), householdId, vehicleId, null, null)).stream()
                .map(WearPartResponse::from)
                .toList();
    }

    @Post
    HttpResponse<WearPartResponse> fit(Authentication user, UUID householdId, UUID vehicleId,
                                       @Body @Valid WearPartRequest request) {
        var command = new WearPartCommand(user.getName(), householdId, vehicleId, null, request.toInput());
        return HttpResponse.created(WearPartResponse.from(fitPart.fit(command)));
    }

    @Put("/{partId}")
    WearPartResponse correct(Authentication user, UUID householdId, UUID vehicleId, UUID partId,
                             @Body @Valid WearPartRequest request) {
        var command = new WearPartCommand(user.getName(), householdId, vehicleId, partId, request.toInput());
        return WearPartResponse.from(correctPart.correct(command));
    }

    @Delete("/{partId}")
    HttpResponse<Void> delete(Authentication user, UUID householdId, UUID vehicleId, UUID partId) {
        deletePart.delete(new WearPartCommand(user.getName(), householdId, vehicleId, partId, null));
        return HttpResponse.noContent();
    }
}
