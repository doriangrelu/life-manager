package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.CategoryEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Accès SQL aux catégories de dépenses et de rentrées (datasource du domaine budget). */
@Repository("budget")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CategoryJdbcRepository extends CrudRepository<CategoryEntity, UUID> {

    /** Catégories du foyer pour un sens, triées par nom, « Autre » (le dernier recours) toujours à la fin. */
    @Query("""
            SELECT * FROM category WHERE household_id = :householdId AND kind = :kind
            ORDER BY lower(name) = 'autre', lower(name)""")
    List<CategoryEntity> findAllByHousehold(UUID householdId, String kind);

    boolean existsByHouseholdIdAndKind(UUID householdId, String kind);

    @Query("""
            SELECT * FROM category
            WHERE household_id = :householdId AND kind = :kind AND lower(name) = lower(:name)""")
    Optional<CategoryEntity> findByName(UUID householdId, String kind, String name);

    /**
     * Crée la catégorie si le nom est libre dans le foyer pour ce sens ; retourne 0 si un autre membre vient de le prendre.
     * Pas d'erreur dans ce cas : la transaction reste utilisable pour relire la catégorie existante.
     */
    @Query("""
            INSERT INTO category (id, household_id, name, archived, kind)
            VALUES (:id, :householdId, :name, :archived, :kind)
            ON CONFLICT (household_id, kind, (lower(name))) DO NOTHING""")
    long insertIfNameFree(UUID id, UUID householdId, String name, boolean archived, String kind);

    @Query("""
            SELECT EXISTS (SELECT 1 FROM category
                           WHERE household_id = :householdId AND kind = :kind
                             AND lower(name) = lower(:name) AND id <> :ignoredId)""")
    boolean isNameTaken(UUID householdId, String kind, String name, UUID ignoredId);
}
