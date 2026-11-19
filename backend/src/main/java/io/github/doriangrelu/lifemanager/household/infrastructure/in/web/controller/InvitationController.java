package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.household.application.port.in.CancelInvitation;
import io.github.doriangrelu.lifemanager.household.application.port.in.IssueInvitation;
import io.github.doriangrelu.lifemanager.household.application.port.in.JoinHousehold;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.HouseholdCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.IssueInvitationCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.JoinHouseholdCommand;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.HouseholdSummaryResponse;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.InvitationResponse;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.JoinHouseholdRequest;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.ratelimit.JoinAttemptLimiter;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.UUID;

/** Invitations : générer un code pour son foyer, rejoindre un foyer avec un code. */
@Controller("/api/households")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class InvitationController {

    private final IssueInvitation issueInvitation;
    private final CancelInvitation cancelInvitation;
    private final JoinHousehold joinHousehold;
    private final JoinAttemptLimiter joinAttempts;

    InvitationController(IssueInvitation issueInvitation, CancelInvitation cancelInvitation,
                         JoinHousehold joinHousehold, JoinAttemptLimiter joinAttempts) {
        this.issueInvitation = issueInvitation;
        this.cancelInvitation = cancelInvitation;
        this.joinHousehold = joinHousehold;
        this.joinAttempts = joinAttempts;
    }

    /** Annule le code en attente du foyer (propriétaire seulement). */
    @Delete("/{householdId}/invitations")
    HttpResponse<Void> cancel(Authentication user, UUID householdId) {
        cancelInvitation.cancel(new HouseholdCommand(user.getName(), householdId));
        return HttpResponse.noContent();
    }

    @Post("/{householdId}/invitations")
    HttpResponse<InvitationResponse> issue(Authentication user, UUID householdId) {
        var invitation = issueInvitation.issue(new IssueInvitationCommand(user.getName(), householdId));
        return HttpResponse.created(InvitationResponse.from(invitation));
    }

    @Post("/join")
    HouseholdSummaryResponse join(Authentication user, @Body @Valid JoinHouseholdRequest request) {
        joinAttempts.consumeAttempt(user.getName());
        return HouseholdSummaryResponse.from(joinHousehold.join(new JoinHouseholdCommand(user.getName(), request.code())));
    }
}
