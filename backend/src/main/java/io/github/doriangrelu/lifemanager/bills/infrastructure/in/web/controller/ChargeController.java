package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.bills.application.port.in.AddCharge;
import io.github.doriangrelu.lifemanager.bills.application.port.in.ListCharges;
import io.github.doriangrelu.lifemanager.bills.application.port.in.TerminateCharge;
import io.github.doriangrelu.lifemanager.bills.application.port.in.UpdateCharge;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.AddChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.UpdateChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.query.ListChargesQuery;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ChargeRequest;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ChargeResponse;
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

/** Charges récurrentes d'un foyer : liste, ajout, modification, résiliation. */
@Controller("/api/households/{householdId}/charges")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ChargeController {

    private final ListCharges listCharges;
    private final AddCharge addCharge;
    private final UpdateCharge updateCharge;
    private final TerminateCharge terminateCharge;

    ChargeController(ListCharges listCharges, AddCharge addCharge, UpdateCharge updateCharge,
                     TerminateCharge terminateCharge) {
        this.listCharges = listCharges;
        this.addCharge = addCharge;
        this.updateCharge = updateCharge;
        this.terminateCharge = terminateCharge;
    }

    @Get
    List<ChargeResponse> list(Authentication user, UUID householdId) {
        return listCharges.list(new ListChargesQuery(user.getName(), householdId)).stream()
                .map(ChargeResponse::from)
                .toList();
    }

    @Post
    HttpResponse<ChargeResponse> add(Authentication user, UUID householdId, @Body @Valid ChargeRequest request) {
        var command = new AddChargeCommand(user.getName(), householdId, request.toInput());
        return HttpResponse.created(ChargeResponse.from(addCharge.add(command)));
    }

    @Put("/{chargeId}")
    ChargeResponse update(Authentication user, UUID householdId, UUID chargeId, @Body @Valid ChargeRequest request) {
        var command = new UpdateChargeCommand(user.getName(), householdId, chargeId, request.toInput());
        return ChargeResponse.from(updateCharge.update(command));
    }

    @Delete("/{chargeId}")
    HttpResponse<Void> terminate(Authentication user, UUID householdId, UUID chargeId) {
        terminateCharge.terminate(new ChargeCommand(user.getName(), householdId, chargeId));
        return HttpResponse.noContent();
    }
}
