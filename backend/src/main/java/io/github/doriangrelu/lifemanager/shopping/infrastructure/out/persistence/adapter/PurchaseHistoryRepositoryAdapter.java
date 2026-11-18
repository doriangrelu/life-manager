package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.shopping.application.port.out.PurchaseHistoryRepository;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.PurchaseHistoryEntry;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.PurchaseHistoryEntryId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.mapper.PurchaseHistoryMapper;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.repository.PurchaseHistoryJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente l'historique des achats avec PostgreSQL (table purchase_history). */
@Singleton
class PurchaseHistoryRepositoryAdapter implements PurchaseHistoryRepository {

    private final PurchaseHistoryJdbcRepository table;

    PurchaseHistoryRepositoryAdapter(PurchaseHistoryJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<PurchaseHistoryEntry> findById(PurchaseHistoryEntryId id) {
        return table.findById(id.value()).map(PurchaseHistoryMapper::toDomain);
    }

    @Override
    public List<PurchaseHistoryEntry> findAllByList(ShoppingListId listId) {
        return table.findAllByList(listId.value()).stream().map(PurchaseHistoryMapper::toDomain).toList();
    }

    @Override
    public void save(PurchaseHistoryEntry entry) {
        var entity = PurchaseHistoryMapper.toEntity(entry);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void delete(PurchaseHistoryEntry entry) {
        table.deleteById(entry.id().value());
    }
}
