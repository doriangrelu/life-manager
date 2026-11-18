package io.github.doriangrelu.lifemanager.kernel.application.port.out;

/** Détermine le vrai type d'un fichier d'après son contenu (jamais d'après son nom ou ce que dit le client). */
public interface ContentTypeDetector {

    /** @return le type MIME détecté, par exemple {@code application/pdf} */
    String detect(byte[] content);
}
