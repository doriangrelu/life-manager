package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.AttachFile;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.DownloadAttachment;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.ListAttachments;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RemoveAttachment;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.AttachFileCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.AttachmentCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.query.ListAttachmentsQuery;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.AttachmentResponse;
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

/** Pièces jointes d'une opération du carnet : ajout (multipart, champ « file »), liste, contenu, suppression. */
@Controller("/api/households/{householdId}/vehicles/{vehicleId}/maintenance/{entryId}/attachments")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class MaintenanceAttachmentController {

    private final AttachFile attachFile;
    private final ListAttachments listAttachments;
    private final DownloadAttachment downloadAttachment;
    private final RemoveAttachment removeAttachment;

    MaintenanceAttachmentController(AttachFile attachFile, ListAttachments listAttachments,
                                    DownloadAttachment downloadAttachment, RemoveAttachment removeAttachment) {
        this.attachFile = attachFile;
        this.listAttachments = listAttachments;
        this.downloadAttachment = downloadAttachment;
        this.removeAttachment = removeAttachment;
    }

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    HttpResponse<AttachmentResponse> attach(Authentication user, UUID householdId, UUID vehicleId, UUID entryId,
                                            CompletedFileUpload file) throws IOException {
        var command = new AttachFileCommand(user.getName(), householdId, vehicleId, entryId, file.getFilename(),
                file.getBytes());
        return HttpResponse.created(AttachmentResponse.from(attachFile.attach(command)));
    }

    @Get
    List<AttachmentResponse> list(Authentication user, UUID householdId, UUID vehicleId, UUID entryId) {
        var query = new ListAttachmentsQuery(user.getName(), householdId, vehicleId, entryId);
        return listAttachments.list(query).stream().map(AttachmentResponse::from).toList();
    }

    /** Contenu du fichier, jamais interprété ni mis en cache par le navigateur. */
    @Get("/{attachmentId}/content")
    HttpResponse<StreamedFile> content(Authentication user, UUID householdId, UUID vehicleId, UUID entryId,
                                       UUID attachmentId) {
        var command = new AttachmentCommand(user.getName(), householdId, vehicleId, entryId, attachmentId);
        var attachment = downloadAttachment.download(command);
        return HttpResponse.ok(new StreamedFile(attachment.content(), MediaType.of(attachment.mediaType())))
                .contentLength(attachment.sizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(attachment.fileName()))
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store");
    }

    @Delete("/{attachmentId}")
    HttpResponse<Void> remove(Authentication user, UUID householdId, UUID vehicleId, UUID entryId,
                              UUID attachmentId) {
        removeAttachment.remove(new AttachmentCommand(user.getName(), householdId, vehicleId, entryId, attachmentId));
        return HttpResponse.noContent();
    }

    /** Nom d'origine : version simplifiée en ASCII pour les vieux navigateurs, version exacte encodée (RFC 6266). */
    private static String contentDisposition(String fileName) {
        var ascii = fileName.replaceAll("[^\\x20-\\x7E]|[\"\\\\]", "_");
        var encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return "attachment; filename=\"" + ascii + "\"; filename*=UTF-8''" + encoded;
    }
}
