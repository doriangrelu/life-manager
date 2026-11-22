package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Deadlines;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.LicensePlate;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Mileage;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.TireSize;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleName;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.VehicleEntity;

/** Conversion entre l'agrégat {@link Vehicle} et sa ligne en base. */
public final class VehicleMapper {

    private VehicleMapper() {
    }

    public static VehicleEntity toEntity(Vehicle vehicle) {
        var details = vehicle.details();
        return new VehicleEntity(
                vehicle.id().value(),
                vehicle.householdId().value(),
                details.name().value(),
                details.optionalLicensePlate().map(LicensePlate::value).orElse(null),
                details.deadlines().technicalInspectionDue(),
                details.deadlines().serviceDue(),
                details.optionalMileage().map(Mileage::kilometers).orElse(null),
                details.optionalTireSize().map(TireSize::value).orElse(null));
    }

    public static Vehicle toDomain(VehicleEntity entity) {
        var details = new VehicleDetails(
                new VehicleName(entity.name()),
                LicensePlate.ofNullable(entity.licensePlate()),
                new Deadlines(entity.technicalInspectionDue(), entity.serviceDue()),
                Mileage.ofNullable(entity.currentMileageKm()),
                TireSize.ofNullable(entity.tireSize()));
        return Vehicle.restore(new VehicleId(entity.id()), new HouseholdId(entity.householdId()), details);
    }
}
