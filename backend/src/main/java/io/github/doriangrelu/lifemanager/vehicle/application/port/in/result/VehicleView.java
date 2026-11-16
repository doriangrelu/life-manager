package io.github.doriangrelu.lifemanager.vehicle.application.port.in.result;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.LicensePlate;

import java.time.LocalDate;
import java.util.UUID;
/** Un véhicule tel que vu hors du domaine ; les champs facultatifs absents valent {@code null}. */
public record VehicleView(UUID id, String name, String licensePlate, LocalDate technicalInspectionDue,
                          LocalDate serviceDue) {

    public static VehicleView from(Vehicle vehicle) {
        var details = vehicle.details();
        return new VehicleView(
                vehicle.id().value(),
                details.name().value(),
                details.optionalLicensePlate().map(LicensePlate::value).orElse(null),
                details.deadlines().technicalInspectionDue(),
                details.deadlines().serviceDue());
    }
}
