package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.entity.ShoppingListEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Accès SQL aux sous-listes de courses (datasource du domaine courses). */
@Repository("shopping")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ShoppingListJdbcRepository extends CrudRepository<ShoppingListEntity, UUID> {

    /** Sous-listes du foyer dans l'ordre choisi par le foyer. */
    @Query("SELECT * FROM shopping_list WHERE household_id = :householdId ORDER BY position, lower(name), id")
    List<ShoppingListEntity> findAllByHousehold(UUID householdId);

    /** Recherche sans tenir compte des majuscules. */
    @Query("SELECT * FROM shopping_list WHERE household_id = :householdId AND lower(name) = lower(:name)")
    Optional<ShoppingListEntity> findByName(UUID householdId, String name);

    boolean existsByHouseholdId(UUID householdId);

    /** Rang d'une sous-liste créée maintenant : après toutes les autres. */
    @Query("SELECT COALESCE(MAX(position) + 1, 0) FROM shopping_list WHERE household_id = :householdId")
    int nextPosition(UUID householdId);

    /** Seul changement possible d'une sous-liste existante : son nom (la position a sa propre requête). */
    @Query("UPDATE shopping_list SET name = :name WHERE id = :id")
    void updateName(UUID id, String name);

    @Query("UPDATE shopping_list SET position = :position WHERE id = :id AND household_id = :householdId")
    void updatePosition(UUID id, UUID householdId, int position);
}
