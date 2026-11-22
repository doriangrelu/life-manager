package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartInput;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Pose ou correction d'une pièce d'usure.
 *
 * @param kind           TIRES_FRONT, TIRES_REAR, PADS_FRONT, PADS_REAR, DISCS_FRONT, DISCS_REAR, BATTERY ou
 *                       TIMING_BELT (ignoré pour une correction)
 * @param label          marque ou modèle, facultatif
 * @param replaceAfterKm à changer tous les … km, facultatif
 */
@Serdeable
public record WearPartRequest(@NotBlank String kind,
                              @Nullable @Size(max = 60) String label,
                              @NotNull LocalDate fittedOn,
                              @NotNull @PositiveOrZero @Max(2_000_000) Integer fittedKm,
                              @Nullable @Positive @Max(500_000) Integer replaceAfterKm) {

    public WearPartInput toInput() {
        return new WearPartInput(kind, label, fittedOn, fittedKm, replaceAfterKm);
    }
}
