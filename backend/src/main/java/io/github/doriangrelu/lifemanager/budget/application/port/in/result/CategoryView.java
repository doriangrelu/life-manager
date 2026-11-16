package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;

import java.util.UUID;
/** Une catégorie telle que vue hors du domaine. */
public record CategoryView(UUID id, String name, boolean archived) {

    public static CategoryView from(Category category) {
        return new CategoryView(category.id().value(), category.name().value(), category.archived());
    }
}
