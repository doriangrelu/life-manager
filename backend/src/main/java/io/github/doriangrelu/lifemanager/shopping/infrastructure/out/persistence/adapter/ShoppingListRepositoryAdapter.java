package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingListRepository;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingList;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ListName;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.mapper.ShoppingListMapper;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.repository.ShoppingListJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/**
 * Implémente le stockage des sous-listes avec PostgreSQL (table shopping_list).
 * Les articles et l'historique d'une sous-liste supprimée partent avec elle (ON DELETE CASCADE).
 */
@Singleton
class ShoppingListRepositoryAdapter implements ShoppingListRepository {

    private final ShoppingListJdbcRepository table;

    ShoppingListRepositoryAdapter(ShoppingListJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<ShoppingList> findById(ShoppingListId id) {
        return table.findById(id.value()).map(ShoppingListMapper::toDomain);
    }

    @Override
    public List<ShoppingList> findAllByHousehold(HouseholdId householdId) {
        return table.findAllByHousehold(householdId.value()).stream().map(ShoppingListMapper::toDomain).toList();
    }

    @Override
    public Optional<ShoppingList> findByName(HouseholdId householdId, ListName name) {
        return table.findByName(householdId.value(), name.value()).map(ShoppingListMapper::toDomain);
    }

    @Override
    public boolean existsForHousehold(HouseholdId householdId) {
        return table.existsByHouseholdId(householdId.value());
    }

    @Override
    public void save(ShoppingList list) {
        var entity = ShoppingListMapper.toEntity(list);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void delete(ShoppingList list) {
        table.deleteById(list.id().value());
    }
}
