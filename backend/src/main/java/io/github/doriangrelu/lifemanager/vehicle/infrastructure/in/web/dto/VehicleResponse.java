package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.VehicleView;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/** Un véhicule, ses échéances, son kilométrage actuel et la taille de ses pneus. */
@Serdeable
public record VehicleResponse(UUID id, String name, @Nullable String licensePlate,
                              @Nullable LocalDate technicalInspectionDue, @Nullable LocalDate serviceDue,
                              @Nullable Integer mileageKm, @Nullable String tireSize) {

    public static VehicleResponse from(VehicleView vehicle) {
        return new VehicleResponse(vehicle.id(), vehicle.name(), vehicle.licensePlate(),
                vehicle.technicalInspectionDue(), vehicle.serviceDue(), vehicle.mileageKm(), vehicle.tireSize());
    }
}
