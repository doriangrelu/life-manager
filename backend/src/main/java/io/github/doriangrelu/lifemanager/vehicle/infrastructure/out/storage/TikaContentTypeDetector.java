package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.storage;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.ContentTypeDetector;
import jakarta.inject.Singleton;
import org.apache.tika.Tika;

/** Détection du type d'un fichier par Apache Tika, d'après ses premiers octets (« signatures »). */
@Singleton
class TikaContentTypeDetector implements ContentTypeDetector {

    private final Tika tika = new Tika();

    @Override
    public String detect(byte[] content) {
        return tika.detect(content);
    }
}
