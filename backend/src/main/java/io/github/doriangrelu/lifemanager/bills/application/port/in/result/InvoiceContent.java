package io.github.doriangrelu.lifemanager.bills.application.port.in.result;

import java.io.InputStream;

/**
 * Contenu d'une facture à renvoyer au navigateur.
 *
 * @param content flux à fermer une fois envoyé
 */
public record InvoiceContent(String fileName, String mediaType, long sizeBytes, InputStream content) {
}
