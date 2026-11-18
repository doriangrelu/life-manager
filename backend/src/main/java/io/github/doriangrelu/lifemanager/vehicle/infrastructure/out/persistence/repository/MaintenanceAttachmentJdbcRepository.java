package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.MaintenanceAttachmentEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux pièces jointes du carnet (datasource du domaine véhicule). */
@Repository("vehicle")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MaintenanceAttachmentJdbcRepository extends CrudRepository<MaintenanceAttachmentEntity, UUID> {

    @Query("SELECT * FROM maintenance_attachment WHERE entry_id = :entryId ORDER BY added_at, id")
    List<MaintenanceAttachmentEntity> findAllByEntry(UUID entryId);

    @Query("""
            SELECT attachment.* FROM maintenance_attachment attachment
            JOIN maintenance_entry entry ON entry.id = attachment.entry_id
            WHERE entry.vehicle_id = :vehicleId""")
    List<MaintenanceAttachmentEntity> findAllByVehicle(UUID vehicleId);

    int countByEntryId(UUID entryId);

    /** Opération de chaque pièce jointe du véhicule (une ligne par pièce jointe). */
    @Query("""
            SELECT attachment.entry_id FROM maintenance_attachment attachment
            JOIN maintenance_entry entry ON entry.id = attachment.entry_id
            WHERE entry.vehicle_id = :vehicleId""")
    List<UUID> findEntryIdsOfVehicle(UUID vehicleId);
}
