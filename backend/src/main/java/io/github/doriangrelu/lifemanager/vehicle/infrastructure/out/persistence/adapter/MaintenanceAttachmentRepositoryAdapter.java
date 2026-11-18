package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceAttachmentRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceAttachment;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.AttachmentId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.mapper.MaintenanceAttachmentMapper;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository.MaintenanceAttachmentJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Implémente le stockage des pièces jointes avec PostgreSQL (table maintenance_attachment). */
@Singleton
class MaintenanceAttachmentRepositoryAdapter implements MaintenanceAttachmentRepository {

    private final MaintenanceAttachmentJdbcRepository table;

    MaintenanceAttachmentRepositoryAdapter(MaintenanceAttachmentJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<MaintenanceAttachment> findById(AttachmentId id) {
        return table.findById(id.value()).map(MaintenanceAttachmentMapper::toDomain);
    }

    @Override
    public List<MaintenanceAttachment> findAllByEntry(MaintenanceEntryId entryId) {
        return table.findAllByEntry(entryId.value()).stream().map(MaintenanceAttachmentMapper::toDomain).toList();
    }

    @Override
    public List<MaintenanceAttachment> findAllByVehicle(VehicleId vehicleId) {
        return table.findAllByVehicle(vehicleId.value()).stream().map(MaintenanceAttachmentMapper::toDomain).toList();
    }

    @Override
    public int countByEntry(MaintenanceEntryId entryId) {
        return table.countByEntryId(entryId.value());
    }

    @Override
    public Map<MaintenanceEntryId, Long> countByEntryOfVehicle(VehicleId vehicleId) {
        // Quelques dizaines de pièces jointes par véhicule au plus : on compte en mémoire.
        return table.findEntryIdsOfVehicle(vehicleId.value()).stream()
                .collect(Collectors.groupingBy(MaintenanceEntryId::new, Collectors.counting()));
    }

    @Override
    public void save(MaintenanceAttachment attachment) {
        table.save(MaintenanceAttachmentMapper.toEntity(attachment));
    }

    @Override
    public void delete(MaintenanceAttachment attachment) {
        table.deleteById(attachment.id().value());
    }
}
