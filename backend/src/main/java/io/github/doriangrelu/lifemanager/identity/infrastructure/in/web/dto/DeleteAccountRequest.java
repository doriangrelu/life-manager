package io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.dto;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

/** Corps de {@code DELETE /api/me} : l'e-mail retapé pour confirmer (vérifié par le domaine). */
@Serdeable
public record DeleteAccountRequest(@Nullable String confirmEmail) {
}
