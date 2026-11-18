package io.github.doriangrelu.lifemanager.vehicle.application.port.out;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceAttachment;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.AttachmentId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Stockage des pièces jointes du carnet (les métadonnées ; le contenu est dans {@link AttachmentStorage}). */
public interface MaintenanceAttachmentRepository {

    Optional<MaintenanceAttachment> findById(AttachmentId id);

    /** Pièces jointes de l'opération, de la plus ancienne à la plus récente. */
    List<MaintenanceAttachment> findAllByEntry(MaintenanceEntryId entryId);

    /** Pièces jointes de toutes les opérations du véhicule. */
    List<MaintenanceAttachment> findAllByVehicle(VehicleId vehicleId);

    int countByEntry(MaintenanceEntryId entryId);

    /** Nombre de pièces jointes de chaque opération du véhicule ; une opération sans pièce jointe est absente. */
    Map<MaintenanceEntryId, Long> countByEntryOfVehicle(VehicleId vehicleId);

    void save(MaintenanceAttachment attachment);

    void delete(MaintenanceAttachment attachment);
}
