package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.WearPartRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.WearPart;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Mileage;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartKind;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartRemoval;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.WearPartEntity;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository.WearPartJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente le stockage des pièces d'usure avec PostgreSQL (table wear_part). */
@Singleton
class WearPartRepositoryAdapter implements WearPartRepository {

    private final WearPartJdbcRepository table;

    WearPartRepositoryAdapter(WearPartJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<WearPart> findById(WearPartId id) {
        return table.findById(id.value()).map(WearPartRepositoryAdapter::toDomain);
    }

    @Override
    public List<WearPart> findAllByVehicle(VehicleId vehicleId) {
        return table.findAllByVehicle(vehicleId.value()).stream().map(WearPartRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<WearPart> findMounted(VehicleId vehicleId, WearPartKind kind) {
        return table.findMounted(vehicleId.value(), kind.name()).map(WearPartRepositoryAdapter::toDomain);
    }

    @Override
    public void save(WearPart part) {
        var entity = toEntity(part);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void delete(WearPart part) {
        table.deleteById(part.id().value());
    }

    private static WearPartEntity toEntity(WearPart part) {
        var details = part.details();
        var removal = part.removal();
        return new WearPartEntity(part.id().value(), part.vehicleId().value(), part.kind().name(), details.label(),
                details.fittedOn(), details.fittedKm().kilometers(), details.replaceAfterKm(),
                removal.map(WearPartRemoval::removedOn).orElse(null),
                removal.map(r -> r.removedKm().kilometers()).orElse(null));
    }

    private static WearPart toDomain(WearPartEntity row) {
        var details = new WearPartDetails(row.label(), row.fittedOn(), new Mileage(row.fittedKm()),
                row.replaceAfterKm());
        var removal = row.removedOn() == null ? null
                : new WearPartRemoval(row.removedOn(), new Mileage(row.removedKm()));
        return WearPart.restore(new WearPartId(row.id()), new VehicleId(row.vehicleId()),
                WearPartKind.of(row.kind()), details, removal);
    }
}
