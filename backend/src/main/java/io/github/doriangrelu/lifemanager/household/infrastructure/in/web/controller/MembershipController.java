package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.household.application.port.in.LeaveHousehold;
import io.github.doriangrelu.lifemanager.household.application.port.in.RemoveMember;
import io.github.doriangrelu.lifemanager.household.application.port.in.TransferOwnership;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.HouseholdCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.MemberCommand;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.NewOwnerRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.UUID;

/** Membres d'un foyer : transmettre la propriété, retirer un membre (propriétaire), quitter le foyer. */
@Controller("/api/households/{householdId}")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class MembershipController {

    private final TransferOwnership transferOwnership;
    private final RemoveMember removeMember;
    private final LeaveHousehold leaveHousehold;

    MembershipController(TransferOwnership transferOwnership, RemoveMember removeMember,
                         LeaveHousehold leaveHousehold) {
        this.transferOwnership = transferOwnership;
        this.removeMember = removeMember;
        this.leaveHousehold = leaveHousehold;
    }

    @Put("/owner")
    HttpResponse<Void> transfer(Authentication user, UUID householdId, @Body @Valid NewOwnerRequest request) {
        transferOwnership.transfer(new MemberCommand(user.getName(), householdId, request.memberId()));
        return HttpResponse.noContent();
    }

    @Delete("/members/{memberId}")
    HttpResponse<Void> remove(Authentication user, UUID householdId, UUID memberId) {
        removeMember.remove(new MemberCommand(user.getName(), householdId, memberId));
        return HttpResponse.noContent();
    }

    @Post("/leave")
    HttpResponse<Void> leave(Authentication user, UUID householdId) {
        leaveHousehold.leave(new HouseholdCommand(user.getName(), householdId));
        return HttpResponse.noContent();
    }
}
