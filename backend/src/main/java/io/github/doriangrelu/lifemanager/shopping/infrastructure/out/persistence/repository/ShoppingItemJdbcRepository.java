package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.entity.ShoppingItemEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux articles de courses (datasource du domaine courses). */
@Repository("shopping")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ShoppingItemJdbcRepository extends CrudRepository<ShoppingItemEntity, UUID> {

    /** Non cochés du plus ancien au plus récent, puis cochés du dernier coché au premier. */
    @Query("""
            SELECT * FROM shopping_item WHERE household_id = :householdId
            ORDER BY checked_at IS NOT NULL, checked_at DESC, added_at, id""")
    List<ShoppingItemEntity> findAllByHousehold(UUID householdId);

    @Query("DELETE FROM shopping_item WHERE household_id = :householdId AND checked_at IS NOT NULL")
    void deleteChecked(UUID householdId);
}
