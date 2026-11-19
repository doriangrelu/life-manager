package io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

/** Corps de {@code PUT /api/me/terms} : la version des conditions lues. */
@Serdeable
public record AcceptTermsRequest(@NotBlank String version) {
}
