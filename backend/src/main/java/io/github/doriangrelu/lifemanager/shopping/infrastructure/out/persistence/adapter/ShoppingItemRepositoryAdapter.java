package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.mapper.ShoppingItemMapper;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.repository.ShoppingItemJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente le stockage des articles de courses avec PostgreSQL (table shopping_item). */
@Singleton
class ShoppingItemRepositoryAdapter implements ShoppingItemRepository {

    private final ShoppingItemJdbcRepository table;

    ShoppingItemRepositoryAdapter(ShoppingItemJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<ShoppingItem> findById(ShoppingItemId id) {
        return table.findById(id.value()).map(ShoppingItemMapper::toDomain);
    }

    @Override
    public List<ShoppingItem> findAllByHousehold(HouseholdId householdId) {
        return table.findAllByHousehold(householdId.value()).stream().map(ShoppingItemMapper::toDomain).toList();
    }

    @Override
    public void save(ShoppingItem item) {
        var entity = ShoppingItemMapper.toEntity(item);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void delete(ShoppingItem item) {
        table.deleteById(item.id().value());
    }

    @Override
    public void deleteChecked(HouseholdId householdId) {
        table.deleteChecked(householdId.value());
    }
}
