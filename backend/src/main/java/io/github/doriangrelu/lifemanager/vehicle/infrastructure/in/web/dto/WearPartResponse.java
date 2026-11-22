package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.WearPartView;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une pièce d'usure : montée si {@code removedOn} est vide ; {@code kmDone} = km parcourus depuis la pose
 * (vide si le kilométrage actuel du véhicule est inconnu).
 */
@Serdeable
public record WearPartResponse(UUID id, String kind, @Nullable String label, LocalDate fittedOn, int fittedKm,
                               @Nullable Integer replaceAfterKm, @Nullable LocalDate removedOn,
                               @Nullable Integer removedKm, @Nullable Integer kmDone) {

    public static WearPartResponse from(WearPartView part) {
        return new WearPartResponse(part.id(), part.kind(), part.label(), part.fittedOn(), part.fittedKm(),
                part.replaceAfterKm(), part.removedOn(), part.removedKm(), part.kmDone());
    }
}
