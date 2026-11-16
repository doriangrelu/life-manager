package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity.InvitationEntity;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

/** Accès SQL à la table des invitations (datasource du domaine household). */
@Repository("household")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface InvitationJdbcRepository extends CrudRepository<InvitationEntity, UUID> {

    Optional<InvitationEntity> findByCode(String code);

    Optional<InvitationEntity> findByHouseholdIdAndStatus(UUID householdId, String status);

    /**
     * Passe l'invitation au statut donné seulement si elle est encore en attente.
     * Retourne 0 si une autre requête l'a déjà utilisée ou annulée : c'est ce qui garantit l'usage unique.
     */
    @Query("UPDATE invitation SET status = :status, accepted_by = :acceptedBy WHERE id = :id AND status = 'PENDING'")
    long closeIfPending(UUID id, String status, @Nullable UUID acceptedBy);
}
