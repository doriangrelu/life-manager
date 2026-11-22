package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.VehicleInput;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Corps d'ajout ou de modification d'un véhicule ; une échéance absente vaut « pas d'échéance ».
 *
 * @param mileageKm kilométrage actuel (facultatif ; relevé aussi par le carnet)
 * @param tireSize  taille des pneus (« 205/55 R16 91V »), facultative
 */
@Serdeable
public record VehicleRequest(@NotBlank @Size(max = 40) String name,
                             @Nullable @Size(max = 12) String licensePlate,
                             @Nullable LocalDate technicalInspectionDue,
                             @Nullable LocalDate serviceDue,
                             @Nullable @PositiveOrZero @Max(2_000_000) Integer mileageKm,
                             @Nullable @Size(max = 30) String tireSize) {

    public VehicleInput toInput() {
        return new VehicleInput(name, licensePlate, technicalInspectionDue, serviceDue, mileageKm, tireSize);
    }
}
