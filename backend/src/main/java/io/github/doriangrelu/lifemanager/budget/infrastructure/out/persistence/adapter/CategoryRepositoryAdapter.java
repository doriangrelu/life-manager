package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.mapper.CategoryMapper;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.CategoryJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente le stockage des catégories avec PostgreSQL (table category). */
@Singleton
class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryJdbcRepository table;

    CategoryRepositoryAdapter(CategoryJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return table.findById(id.value()).map(CategoryMapper::toDomain);
    }

    @Override
    public List<Category> findAllByHousehold(HouseholdId householdId) {
        return table.findAllByHousehold(householdId.value()).stream().map(CategoryMapper::toDomain).toList();
    }

    @Override
    public boolean existsForHousehold(HouseholdId householdId) {
        return table.existsByHouseholdId(householdId.value());
    }

    @Override
    public boolean isNameTaken(HouseholdId householdId, CategoryName name, CategoryId ignored) {
        return table.isNameTaken(householdId.value(), name.value(), ignored.value());
    }

    @Override
    public void save(Category category) {
        var entity = CategoryMapper.toEntity(category);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }
}
