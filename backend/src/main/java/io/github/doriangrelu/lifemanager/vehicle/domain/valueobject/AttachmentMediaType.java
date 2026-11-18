package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.util.Arrays;

/** Types de fichiers acceptés dans le carnet : factures et photos. */
public enum AttachmentMediaType {
    PDF("application/pdf"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    WEBP("image/webp");

    private final String mimeType;

    AttachmentMediaType(String mimeType) {
        this.mimeType = mimeType;
    }

    /** @throws InvalidValueException si le type détecté n'est pas accepté */
    public static AttachmentMediaType ofMimeType(String detected) {
        return Arrays.stream(values())
                .filter(type -> type.mimeType.equals(detected))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Type de fichier non accepté (PDF, JPEG, PNG ou WebP)"));
    }

    public String mimeType() {
        return mimeType;
    }
}
