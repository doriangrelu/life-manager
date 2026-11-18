package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.regex.Pattern;

/**
 * Nom d'origine d'une pièce jointe, nettoyé : sans chemin, sans caractère de contrôle, 120 caractères au plus
 * (un nom trop long est raccourci en gardant son extension).
 */
public record AttachmentFileName(String value) {

    private static final String FIELD = "Le nom du fichier";
    private static final int MAX_LENGTH = 120;
    private static final Pattern CONTROL = Pattern.compile("\\p{Cntrl}");

    public AttachmentFileName {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }

    /** Nettoie le nom envoyé par le navigateur (« C:\\scans\\facture.pdf » → « facture.pdf »). */
    public static AttachmentFileName clean(String raw) {
        var name = Require.notNull(raw, FIELD);
        name = name.substring(Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\')) + 1);
        name = CONTROL.matcher(name).replaceAll("").strip();
        return new AttachmentFileName(shorten(name));
    }

    private static String shorten(String name) {
        if (name.length() <= MAX_LENGTH) {
            return name;
        }
        int dot = name.lastIndexOf('.');
        var extension = dot > 0 && name.length() - dot <= 10 ? name.substring(dot) : "";
        return name.substring(0, MAX_LENGTH - extension.length()) + extension;
    }
}
