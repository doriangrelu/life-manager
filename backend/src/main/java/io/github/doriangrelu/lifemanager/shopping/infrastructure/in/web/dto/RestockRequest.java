package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/** Corps de remise en liste : les éléments de l'historique choisis (100 au plus). */
@Serdeable
public record RestockRequest(@NotEmpty @Size(max = 100) List<@NotNull UUID> entryIds) {
}
