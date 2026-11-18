package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.entity.PurchaseHistoryEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL à l'historique des achats (datasource du domaine courses). */
@Repository("shopping")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PurchaseHistoryJdbcRepository extends CrudRepository<PurchaseHistoryEntity, UUID> {

    /** Du plus récemment acheté au plus ancien. */
    @Query("SELECT * FROM purchase_history WHERE list_id = :listId ORDER BY last_purchased_at DESC, lower(label)")
    List<PurchaseHistoryEntity> findAllByList(UUID listId);
}
