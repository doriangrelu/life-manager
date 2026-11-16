package io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.identity.application.port.in.GetCurrentUser;
import io.github.doriangrelu.lifemanager.identity.application.port.in.GetCurrentUserQuery;
import io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.dto.UserProfileResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

/** Profil de l'utilisateur connecté : remplace le « userinfo » de Keycloak. */
@Controller("/api/whoami")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class WhoAmIController {

    private final GetCurrentUser getCurrentUser;

    WhoAmIController(GetCurrentUser getCurrentUser) {
        this.getCurrentUser = getCurrentUser;
    }

    @Get
    UserProfileResponse whoami(Authentication authentication) {
        var profile = getCurrentUser.get(new GetCurrentUserQuery(authentication.getName()));
        return UserProfileResponse.from(profile);
    }
}
