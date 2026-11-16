package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Cost;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceKind;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceNote;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Mileage;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.MaintenanceEntryEntity;

/** Conversion entre l'agrégat {@link MaintenanceEntry} et sa ligne en base. */
public final class MaintenanceEntryMapper {

    private MaintenanceEntryMapper() {
    }

    public static MaintenanceEntryEntity toEntity(MaintenanceEntry entry) {
        var details = entry.details();
        return new MaintenanceEntryEntity(
                entry.id().value(),
                entry.vehicleId().value(),
                details.performedOn(),
                details.kind().name(),
                details.optionalMileage().map(Mileage::kilometers).orElse(null),
                details.optionalCost().map(Cost::cents).orElse(null),
                details.optionalNote().map(MaintenanceNote::value).orElse(null),
                entry.recordedBy().value(),
                entry.recordedAt());
    }

    public static MaintenanceEntry toDomain(MaintenanceEntryEntity entity) {
        var details = new MaintenanceDetails(
                entity.performedOn(),
                MaintenanceKind.of(entity.kind()),
                Mileage.ofNullable(entity.mileageKm()),
                Cost.ofNullable(entity.costCents()),
                MaintenanceNote.ofNullable(entity.note()));
        return MaintenanceEntry.restore(new MaintenanceEntryId(entity.id()), new VehicleId(entity.vehicleId()),
                details, new MemberId(entity.recordedBy()), entity.recordedAt());
    }
}
