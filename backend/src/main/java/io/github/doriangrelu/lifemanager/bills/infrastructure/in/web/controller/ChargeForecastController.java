package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.bills.application.port.in.ForecastCharges;
import io.github.doriangrelu.lifemanager.bills.application.port.in.query.ForecastQuery;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.ForecastResponse;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

/** Échéances attendues d'une période ({@code GET …/charges/forecast?from=2026-11-01&to=2026-11-30}). */
@Controller("/api/households/{householdId}/charges/forecast")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ChargeForecastController {

    private final ForecastCharges forecastCharges;

    ChargeForecastController(ForecastCharges forecastCharges) {
        this.forecastCharges = forecastCharges;
    }

    @Get
    List<ForecastResponse> forecast(Authentication user, UUID householdId, @Nullable @QueryValue String from,
                                    @Nullable @QueryValue String to) {
        var query = new ForecastQuery(user.getName(), householdId, parse(from), parse(to));
        return forecastCharges.forecast(query).stream().map(ForecastResponse::from).toList();
    }

    // Jour absent : null, refusé par le use case après le contrôle d'accès.
    private static LocalDate parse(String day) {
        try {
            return day == null ? null : LocalDate.parse(day);
        } catch (DateTimeParseException invalid) {
            throw new InvalidValueException("La date doit être au format AAAA-MM-JJ");
        }
    }
}
