package io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.identity.application.port.in.AcceptTerms;
import io.github.doriangrelu.lifemanager.identity.application.port.in.DeleteMyAccount;
import io.github.doriangrelu.lifemanager.identity.application.port.in.ExportMyData;
import io.github.doriangrelu.lifemanager.identity.application.port.in.command.AcceptTermsCommand;
import io.github.doriangrelu.lifemanager.identity.application.port.in.command.DeleteMyAccountCommand;
import io.github.doriangrelu.lifemanager.identity.application.port.in.query.GetCurrentUserQuery;
import io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.dto.AcceptTermsRequest;
import io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.dto.DeleteAccountRequest;
import io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.ratelimit.ExportLimiter;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Map;

/** Compte de l'utilisateur connecté : conditions acceptées, export de ses données, suppression. */
@Controller("/api/me")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class MyAccountController {

    private final AcceptTerms acceptTerms;
    private final ExportMyData exportMyData;
    private final DeleteMyAccount deleteMyAccount;
    private final ExportLimiter exportLimiter;
    private final Clock clock;

    MyAccountController(AcceptTerms acceptTerms, ExportMyData exportMyData, DeleteMyAccount deleteMyAccount,
                        ExportLimiter exportLimiter, Clock clock) {
        this.acceptTerms = acceptTerms;
        this.exportMyData = exportMyData;
        this.deleteMyAccount = deleteMyAccount;
        this.exportLimiter = exportLimiter;
        this.clock = clock;
    }

    @Put("/terms")
    HttpResponse<Void> acceptTerms(Authentication user, @Body @Valid AcceptTermsRequest request) {
        acceptTerms.accept(new AcceptTermsCommand(user.getName(), request.version()));
        return HttpResponse.noContent();
    }

    /** Fichier JSON en pièce jointe, produit à la demande (rien n'est gardé sur le serveur). */
    @Get("/export")
    HttpResponse<Map<String, Object>> export(Authentication user) {
        exportLimiter.consume(user.getName());
        var data = exportMyData.export(new GetCurrentUserQuery(user.getName()));
        var fileName = "life-manager-mes-donnees-" + LocalDate.now(clock) + ".json";
        return HttpResponse.ok(data)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
    }

    @Delete
    HttpResponse<Void> delete(Authentication user, @Body @Valid DeleteAccountRequest request) {
        deleteMyAccount.delete(new DeleteMyAccountCommand(user.getName(), request.confirmEmail()));
        return HttpResponse.noContent();
    }
}
