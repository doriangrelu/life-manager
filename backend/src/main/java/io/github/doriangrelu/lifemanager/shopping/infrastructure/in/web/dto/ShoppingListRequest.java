package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corps d'ajout ou de renommage d'une sous-liste. */
@Serdeable
public record ShoppingListRequest(@NotBlank @Size(max = 30) String name) {
}
