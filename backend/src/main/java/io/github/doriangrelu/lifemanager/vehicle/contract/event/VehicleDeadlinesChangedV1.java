package io.github.doriangrelu.lifemanager.vehicle.contract.event;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Événement public : un véhicule a été ajouté ou modifié ; porte l'état complet de ses échéances
 * ({@code null} = pas d'échéance). Contrat stable entre domaines (types simples, sérialisable en JSON).
 */
@Serdeable
public record VehicleDeadlinesChangedV1(UUID vehicleId, UUID householdId, String vehicleName,
                                        @Nullable LocalDate technicalInspectionDue, @Nullable LocalDate serviceDue,
                                        Instant occurredAt) {
}
