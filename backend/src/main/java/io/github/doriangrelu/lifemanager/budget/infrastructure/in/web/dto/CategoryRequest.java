package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corps d'ajout ou de renommage d'une catégorie. */
@Serdeable
public record CategoryRequest(@NotBlank @Size(max = 40) String name) {
}
