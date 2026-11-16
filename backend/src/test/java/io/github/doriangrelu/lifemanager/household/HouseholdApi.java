package io.github.doriangrelu.lifemanager.household;

import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.HouseholdDetailsResponse;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.HouseholdSummaryResponse;
import io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto.InvitationResponse;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowableOfType;

/** Petit client de test pour l'API des foyers : un appel = une méthode lisible. */
record HouseholdApi(BlockingHttpClient http) {

    HouseholdSummaryResponse create(String token, String name) {
        return http.retrieve(HttpRequest.POST("/api/households", Map.of("name", name)).bearerAuth(token),
                HouseholdSummaryResponse.class);
    }

    List<HouseholdSummaryResponse> list(String token) {
        return http.retrieve(HttpRequest.GET("/api/households").bearerAuth(token),
                Argument.listOf(HouseholdSummaryResponse.class));
    }

    HouseholdDetailsResponse get(String token, UUID householdId) {
        return http.retrieve(HttpRequest.GET("/api/households/" + householdId).bearerAuth(token),
                HouseholdDetailsResponse.class);
    }

    InvitationResponse issueInvitation(String token, UUID householdId) {
        return http.retrieve(HttpRequest.POST("/api/households/" + householdId + "/invitations", "")
                .bearerAuth(token), InvitationResponse.class);
    }

    HouseholdSummaryResponse join(String token, String code) {
        return http.retrieve(HttpRequest.POST("/api/households/join", Map.of("code", code)).bearerAuth(token),
                HouseholdSummaryResponse.class);
    }

    /** Exécute l'appel, qui doit échouer, et retourne l'erreur HTTP obtenue. */
    static HttpClientResponseException failure(Runnable call) {
        return catchThrowableOfType(HttpClientResponseException.class, call::run);
    }
}
