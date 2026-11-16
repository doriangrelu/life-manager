package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.ListVehicles;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RegisterVehicle;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RemoveVehicle;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.UpdateVehicle;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RegisterVehicleCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RemoveVehicleCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.UpdateVehicleCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.query.ListVehiclesQuery;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleRequest;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleResponse;
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

/** Véhicules d'un foyer : liste, ajout, modification (dont échéances), suppression. */
@Controller("/api/households/{householdId}/vehicles")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class VehicleController {

    private final ListVehicles listVehicles;
    private final RegisterVehicle registerVehicle;
    private final UpdateVehicle updateVehicle;
    private final RemoveVehicle removeVehicle;

    VehicleController(ListVehicles listVehicles, RegisterVehicle registerVehicle, UpdateVehicle updateVehicle,
                      RemoveVehicle removeVehicle) {
        this.listVehicles = listVehicles;
        this.registerVehicle = registerVehicle;
        this.updateVehicle = updateVehicle;
        this.removeVehicle = removeVehicle;
    }

    @Get
    List<VehicleResponse> list(Authentication user, UUID householdId) {
        return listVehicles.list(new ListVehiclesQuery(user.getName(), householdId)).stream()
                .map(VehicleResponse::from)
                .toList();
    }

    @Post
    HttpResponse<VehicleResponse> register(Authentication user, UUID householdId,
                                           @Body @Valid VehicleRequest request) {
        var command = new RegisterVehicleCommand(user.getName(), householdId, request.toInput());
        return HttpResponse.created(VehicleResponse.from(registerVehicle.register(command)));
    }

    @Put("/{vehicleId}")
    VehicleResponse update(Authentication user, UUID householdId, UUID vehicleId,
                           @Body @Valid VehicleRequest request) {
        var command = new UpdateVehicleCommand(user.getName(), householdId, vehicleId, request.toInput());
        return VehicleResponse.from(updateVehicle.update(command));
    }

    @Delete("/{vehicleId}")
    HttpResponse<Void> remove(Authentication user, UUID householdId, UUID vehicleId) {
        removeVehicle.remove(new RemoveVehicleCommand(user.getName(), householdId, vehicleId));
        return HttpResponse.noContent();
    }
}
