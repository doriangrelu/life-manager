package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/** Corps d'un réordonnancement : identifiants dans l'ordre voulu. */
@Serdeable
public record OrderRequest(@NotNull @Size(max = 500) List<@NotNull UUID> ids) {
}
