package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.mapper.ShoppingItemMapper;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.repository.ShoppingItemJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<ShoppingItem> findAllByList(ShoppingListId listId) {
        return table.findAllByList(listId.value()).stream().map(ShoppingItemMapper::toDomain).toList();
    }

    @Override
    public List<ShoppingItem> findCheckedByList(ShoppingListId listId) {
        return table.findCheckedByList(listId.value()).stream().map(ShoppingItemMapper::toDomain).toList();
    }

    @Override
    public Map<ShoppingListId, Long> countUncheckedByList(HouseholdId householdId) {
        // Un foyer a peu d'articles en attente : on compte en mémoire.
        return table.findUncheckedListIds(householdId.value()).stream()
                .collect(Collectors.groupingBy(ShoppingListId::new, Collectors.counting()));
    }

    @Override
    public void save(ShoppingItem item) {
        var id = item.id().value();
        if (table.existsById(id)) {
            table.updateCheckedAt(id, item.checkedAt().orElse(null));
        } else {
            // Un nouvel article arrive en fin de sous-liste.
            table.save(ShoppingItemMapper.toNewEntity(item, table.nextPosition(item.listId().value())));
        }
    }

    @Override
    public void reorder(ShoppingListId listId, List<ShoppingItemId> ordered) {
        var requested = ordered.stream().map(ShoppingItemId::value).toList();
        var all = Ordering.merge(table.findIdsInOrder(listId.value()), requested);
        for (int position = 0; position < all.size(); position++) {
            table.updatePosition(all.get(position), listId.value(), position);
        }
    }

    @Override
    public void delete(ShoppingItem item) {
        table.deleteById(item.id().value());
    }

    @Override
    public void deleteChecked(ShoppingListId listId) {
        table.deleteChecked(listId.value());
    }
}
