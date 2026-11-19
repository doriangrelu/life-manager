package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.entity.ShoppingItemEntity;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Accès SQL aux articles de courses (datasource du domaine courses). */
@Repository("shopping")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ShoppingItemJdbcRepository extends CrudRepository<ShoppingItemEntity, UUID> {

    /** Non cochés dans l'ordre choisi par le foyer, puis cochés du dernier coché au premier. */
    @Query("""
            SELECT * FROM shopping_item WHERE list_id = :listId
            ORDER BY checked_at IS NOT NULL, checked_at DESC, position, added_at, id""")
    List<ShoppingItemEntity> findAllByList(UUID listId);

    @Query("SELECT * FROM shopping_item WHERE list_id = :listId AND checked_at IS NOT NULL")
    List<ShoppingItemEntity> findCheckedByList(UUID listId);

    /** Sous-liste de chaque article non coché du foyer (une ligne par article). */
    @Query("""
            SELECT item.list_id FROM shopping_item item JOIN shopping_list list ON list.id = item.list_id
            WHERE list.household_id = :householdId AND item.checked_at IS NULL""")
    List<UUID> findUncheckedListIds(UUID householdId);

    /** Identifiants des articles de la sous-liste dans leur ordre actuel. */
    @Query("SELECT id FROM shopping_item WHERE list_id = :listId ORDER BY position, added_at, id")
    List<UUID> findIdsInOrder(UUID listId);

    /** Rang d'un article ajouté maintenant : après tous les autres. */
    @Query("SELECT COALESCE(MAX(position) + 1, 0) FROM shopping_item WHERE list_id = :listId")
    int nextPosition(UUID listId);

    /** Seul changement possible d'un article existant : coché ou non (la position a sa propre requête). */
    @Query("UPDATE shopping_item SET checked_at = :checkedAt WHERE id = :id")
    void updateCheckedAt(UUID id, @Nullable Instant checkedAt);

    @Query("UPDATE shopping_item SET position = :position WHERE id = :id AND list_id = :listId")
    void updatePosition(UUID id, UUID listId, int position);

    @Query("DELETE FROM shopping_item WHERE list_id = :listId AND checked_at IS NOT NULL")
    void deleteChecked(UUID listId);
}
