package io.github.doriangrelu.lifemanager.vehicle.application.port.in.result;

import java.io.InputStream;

/**
 * Contenu d'une pièce jointe à renvoyer au navigateur.
 *
 * @param content flux à fermer une fois envoyé
 */
public record AttachmentContent(String fileName, String mediaType, long sizeBytes, InputStream content) {
}
