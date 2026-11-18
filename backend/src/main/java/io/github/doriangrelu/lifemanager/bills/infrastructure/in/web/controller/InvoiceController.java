package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.bills.application.port.in.AttachInvoice;
import io.github.doriangrelu.lifemanager.bills.application.port.in.DownloadInvoice;
import io.github.doriangrelu.lifemanager.bills.application.port.in.ListInvoices;
import io.github.doriangrelu.lifemanager.bills.application.port.in.RemoveInvoice;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.AttachInvoiceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.InvoiceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.OccurrenceCommand;
import io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto.InvoiceResponse;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/** Factures d'une échéance : ajout (multipart, champ « file »), liste, contenu, suppression. */
@Controller("/api/households/{householdId}/charges/{chargeId}/occurrences/{occurrenceId}/invoices")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class InvoiceController {

    private final AttachInvoice attachInvoice;
    private final ListInvoices listInvoices;
    private final DownloadInvoice downloadInvoice;
    private final RemoveInvoice removeInvoice;

    InvoiceController(AttachInvoice attachInvoice, ListInvoices listInvoices, DownloadInvoice downloadInvoice,
                      RemoveInvoice removeInvoice) {
        this.attachInvoice = attachInvoice;
        this.listInvoices = listInvoices;
        this.downloadInvoice = downloadInvoice;
        this.removeInvoice = removeInvoice;
    }

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    HttpResponse<InvoiceResponse> attach(Authentication user, UUID householdId, UUID chargeId, UUID occurrenceId,
                                         CompletedFileUpload file) throws IOException {
        var target = new OccurrenceCommand(user.getName(), householdId, chargeId, occurrenceId);
        var command = new AttachInvoiceCommand(target, file.getFilename(), file.getBytes());
        return HttpResponse.created(InvoiceResponse.from(attachInvoice.attach(command)));
    }

    @Get
    List<InvoiceResponse> list(Authentication user, UUID householdId, UUID chargeId, UUID occurrenceId) {
        var target = new OccurrenceCommand(user.getName(), householdId, chargeId, occurrenceId);
        return listInvoices.list(target).stream().map(InvoiceResponse::from).toList();
    }

    /** Contenu du fichier, jamais interprété ni mis en cache par le navigateur. */
    @Get("/{invoiceId}/content")
    HttpResponse<StreamedFile> content(Authentication user, UUID householdId, UUID chargeId, UUID occurrenceId,
                                       UUID invoiceId) {
        var target = new OccurrenceCommand(user.getName(), householdId, chargeId, occurrenceId);
        var invoice = downloadInvoice.download(new InvoiceCommand(target, invoiceId));
        return HttpResponse.ok(new StreamedFile(invoice.content(), MediaType.of(invoice.mediaType())))
                .contentLength(invoice.sizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(invoice.fileName()))
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store");
    }

    @Delete("/{invoiceId}")
    HttpResponse<Void> remove(Authentication user, UUID householdId, UUID chargeId, UUID occurrenceId,
                              UUID invoiceId) {
        var target = new OccurrenceCommand(user.getName(), householdId, chargeId, occurrenceId);
        removeInvoice.remove(new InvoiceCommand(target, invoiceId));
        return HttpResponse.noContent();
    }

    /** Nom d'origine : version simplifiée en ASCII pour les vieux navigateurs, version exacte encodée (RFC 6266). */
    private static String contentDisposition(String fileName) {
        var ascii = fileName.replaceAll("[^\\x20-\\x7E]|[\"\\\\]", "_");
        var encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return "attachment; filename=\"" + ascii + "\"; filename*=UTF-8''" + encoded;
    }
}
