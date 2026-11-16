package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.MaintenanceEntryView;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/** Une opération du carnet d'entretien. Coût en centimes d'euro. */
@Serdeable
public record MaintenanceResponse(UUID id, LocalDate performedOn, String kind, @Nullable Integer mileageKm,
                                  @Nullable Long costCents, @Nullable String note) {

    public static MaintenanceResponse from(MaintenanceEntryView entry) {
        return new MaintenanceResponse(entry.id(), entry.performedOn(), entry.kind(), entry.mileageKm(),
                entry.costCents(), entry.note());
    }
}
