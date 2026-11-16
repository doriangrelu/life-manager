package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.DeleteMaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.ListMaintenanceEntries;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RecordMaintenance;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.DeleteMaintenanceEntryCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RecordMaintenanceCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.query.ListMaintenanceEntriesQuery;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.MaintenanceRequest;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.MaintenanceResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
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

/** Carnet d'entretien d'un véhicule : consultation, ajout et suppression d'opérations. */
@Controller("/api/households/{householdId}/vehicles/{vehicleId}/maintenance")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class MaintenanceController {

    private final ListMaintenanceEntries listEntries;
    private final RecordMaintenance recordMaintenance;
    private final DeleteMaintenanceEntry deleteEntry;

    MaintenanceController(ListMaintenanceEntries listEntries, RecordMaintenance recordMaintenance,
                          DeleteMaintenanceEntry deleteEntry) {
        this.listEntries = listEntries;
        this.recordMaintenance = recordMaintenance;
        this.deleteEntry = deleteEntry;
    }

    @Get
    List<MaintenanceResponse> list(Authentication user, UUID householdId, UUID vehicleId) {
        return listEntries.list(new ListMaintenanceEntriesQuery(user.getName(), householdId, vehicleId)).stream()
                .map(MaintenanceResponse::from)
                .toList();
    }

    @Post
    HttpResponse<MaintenanceResponse> record(Authentication user, UUID householdId, UUID vehicleId,
                                             @Body @Valid MaintenanceRequest request) {
        var command = new RecordMaintenanceCommand(user.getName(), householdId, vehicleId, request.toInput());
        return HttpResponse.created(MaintenanceResponse.from(recordMaintenance.record(command)));
    }

    @Delete("/{entryId}")
    HttpResponse<Void> delete(Authentication user, UUID householdId, UUID vehicleId, UUID entryId) {
        deleteEntry.delete(new DeleteMaintenanceEntryCommand(user.getName(), householdId, vehicleId, entryId));
        return HttpResponse.noContent();
    }
}
