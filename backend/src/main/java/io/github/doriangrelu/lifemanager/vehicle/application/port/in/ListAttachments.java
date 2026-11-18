package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.query.ListAttachmentsQuery;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.AttachmentView;

import java.util.List;

/** Pièces jointes d'une opération du carnet, de la plus ancienne à la plus récente. */
public interface ListAttachments {

    List<AttachmentView> list(ListAttachmentsQuery query);
}
