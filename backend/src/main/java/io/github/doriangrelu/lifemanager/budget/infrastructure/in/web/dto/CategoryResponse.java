package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

/** Une catégorie de dépenses. */
@Serdeable
public record CategoryResponse(UUID id, String name, boolean archived) {

    public static CategoryResponse from(CategoryView category) {
        return new CategoryResponse(category.id(), category.name(), category.archived());
    }
}
