package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.MaintenanceInput;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Corps d'ajout d'une opération d'entretien.
 *
 * @param kind       type : SERVICE, OIL_CHANGE, TECHNICAL_INSPECTION, TIRES, REPAIR ou OTHER
 * @param costCents  coût en centimes d'euro (189,90 € = 18990)
 * @param nextDueOn  prochaine échéance précise (contrôle technique, révision) ; absente = calcul automatique
 * @param otherLabel précision du type OTHER (« Essuie-glaces ») ; refusée pour les autres types
 */
@Serdeable
public record MaintenanceRequest(@NotNull LocalDate performedOn,
                                 @NotBlank String kind,
                                 @Nullable @PositiveOrZero @Max(2_000_000) Integer mileageKm,
                                 @Nullable @PositiveOrZero @Max(10_000_000) Long costCents,
                                 @Nullable @Size(max = 140) String note,
                                 @Nullable LocalDate nextDueOn,
                                 @Nullable @Size(max = 40) String otherLabel) {

    public MaintenanceInput toInput() {
        return new MaintenanceInput(performedOn, kind, mileageKm, costCents, note, nextDueOn, otherLabel);
    }
}
