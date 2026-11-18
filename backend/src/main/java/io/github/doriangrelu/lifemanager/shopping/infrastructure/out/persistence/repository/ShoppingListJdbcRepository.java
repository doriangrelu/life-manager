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

    @Query("SELECT * FROM shopping_list WHERE household_id = :householdId ORDER BY lower(name), id")
    List<ShoppingListEntity> findAllByHousehold(UUID householdId);

    /** Recherche sans tenir compte des majuscules. */
    @Query("SELECT * FROM shopping_list WHERE household_id = :householdId AND lower(name) = lower(:name)")
    Optional<ShoppingListEntity> findByName(UUID householdId, String name);

    boolean existsByHouseholdId(UUID householdId);
}
