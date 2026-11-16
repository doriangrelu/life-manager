package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.CategoryEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux catégories de dépenses (datasource du domaine budget). */
@Repository("budget")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CategoryJdbcRepository extends CrudRepository<CategoryEntity, UUID> {

    @Query("SELECT * FROM category WHERE household_id = :householdId ORDER BY lower(name)")
    List<CategoryEntity> findAllByHousehold(UUID householdId);

    boolean existsByHouseholdId(UUID householdId);

    @Query("""
            SELECT EXISTS (SELECT 1 FROM category
                           WHERE household_id = :householdId AND lower(name) = lower(:name) AND id <> :ignoredId)""")
    boolean isNameTaken(UUID householdId, String name, UUID ignoredId);
}
