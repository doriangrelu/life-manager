package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.CategoryBudgetEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Accès SQL aux règles de budget prévu (datasource du domaine budget). */
@Repository("budget")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CategoryBudgetJdbcRepository extends GenericRepository<CategoryBudgetEntity, UUID> {

    /**
     * Crée la règle, ou remplace le montant de celle de même catégorie, mois et portée.
     * Les paramètres de la requête sont les champs de la ligne passée.
     */
    @Query("""
            INSERT INTO category_budget (id, household_id, category_id, month, scope, planned_cents)
            VALUES (:id, :householdId, :categoryId, :month, :scope, :plannedCents)
            ON CONFLICT (category_id, month, scope) DO UPDATE SET planned_cents = EXCLUDED.planned_cents""")
    void upsert(CategoryBudgetEntity rule);

    @Query("DELETE FROM category_budget WHERE category_id = :categoryId AND month = :month AND scope = :scope")
    void deleteRule(UUID categoryId, LocalDate month, String scope);

    @Query("SELECT * FROM category_budget WHERE household_id = :householdId AND month <= :month")
    List<CategoryBudgetEntity> findAllUntil(UUID householdId, LocalDate month);
}
