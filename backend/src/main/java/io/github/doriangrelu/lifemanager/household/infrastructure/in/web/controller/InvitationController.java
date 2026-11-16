package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.household.application.port.in.IssueInvitation;
import io.github.doriangrelu.lifemanager.household.application.port.in.JoinHousehold;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.IssueInvitationCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.JoinHouseholdCommand;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.HouseholdSummaryResponse;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.InvitationResponse;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.JoinHouseholdRequest;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.ratelimit.JoinAttemptLimiter;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
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
    private final JoinHousehold joinHousehold;
    private final JoinAttemptLimiter joinAttempts;

    InvitationController(IssueInvitation issueInvitation, JoinHousehold joinHousehold,
                         JoinAttemptLimiter joinAttempts) {
        this.issueInvitation = issueInvitation;
        this.joinHousehold = joinHousehold;
        this.joinAttempts = joinAttempts;
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
