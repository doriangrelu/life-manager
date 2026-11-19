package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.CategoryEntity;

/** Conversion entre l'agrégat {@link Category} et sa ligne en base. */
public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryEntity toEntity(Category category) {
        return new CategoryEntity(category.id().value(), category.householdId().value(),
                category.name().value(), category.archived(), category.kind().name());
    }

    public static Category toDomain(CategoryEntity entity) {
        return Category.restore(new CategoryId(entity.id()), new HouseholdId(entity.householdId()),
                new CategoryName(entity.name()), entity.archived(), CategoryKind.valueOf(entity.kind()));
    }
}
