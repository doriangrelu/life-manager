package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.bills.application.port.in.ConfirmOccurrence;
import io.github.doriangrelu.lifemanager.bills.application.port.in.ListOccurrences;
import io.github.doriangrelu.lifemanager.bills.application.port.in.SkipOccurrence;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ConfirmOccurrenceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.OccurrenceCommand;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ConfirmRequest;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.OccurrenceResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;
import java.util.UUID;

/** Échéances d'une charge : liste, confirmation du montant réel, saut. */
@Controller("/api/households/{householdId}/charges/{chargeId}/occurrences")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class OccurrenceController {

    private final ListOccurrences listOccurrences;
    private final ConfirmOccurrence confirmOccurrence;
    private final SkipOccurrence skipOccurrence;

    OccurrenceController(ListOccurrences listOccurrences, ConfirmOccurrence confirmOccurrence,
                         SkipOccurrence skipOccurrence) {
        this.listOccurrences = listOccurrences;
        this.confirmOccurrence = confirmOccurrence;
        this.skipOccurrence = skipOccurrence;
    }

    @Get
    List<OccurrenceResponse> list(Authentication user, UUID householdId, UUID chargeId) {
        return listOccurrences.list(new ChargeCommand(user.getName(), householdId, chargeId)).stream()
                .map(OccurrenceResponse::from)
                .toList();
    }

    @Post("/{occurrenceId}/confirm")
    OccurrenceResponse confirm(Authentication user, UUID householdId, UUID chargeId, UUID occurrenceId,
                               @Body ConfirmRequest request) {
        var target = new OccurrenceCommand(user.getName(), householdId, chargeId, occurrenceId);
        return OccurrenceResponse.from(confirmOccurrence.confirm(new ConfirmOccurrenceCommand(target,
                request.amountCents())));
    }

    @Post("/{occurrenceId}/skip")
    OccurrenceResponse skip(Authentication user, UUID householdId, UUID chargeId, UUID occurrenceId) {
        var target = new OccurrenceCommand(user.getName(), householdId, chargeId, occurrenceId);
        return OccurrenceResponse.from(skipOccurrence.skip(target));
    }
}
