package io.github.doriangrelu.lifemanager.vehicle.application.port.in.result;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.WearPart;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Mileage;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une pièce d'usure telle que vue hors du domaine ; champs absents à {@code null}.
 *
 * @param removedOn dépose (pièce remplacée), ou {@code null} si elle est montée
 * @param kmDone    km parcourus depuis la pose, ou {@code null} si le kilométrage actuel est inconnu
 */
public record WearPartView(UUID id, String kind, String label, LocalDate fittedOn, int fittedKm,
                           Integer replaceAfterKm, LocalDate removedOn, Integer removedKm, Integer kmDone) {

    public static WearPartView from(WearPart part, Mileage currentMileage) {
        var details = part.details();
        var removal = part.removal();
        return new WearPartView(part.id().value(), part.kind().name(), details.label(), details.fittedOn(),
                details.fittedKm().kilometers(), details.replaceAfterKm(),
                removal.map(r -> r.removedOn()).orElse(null),
                removal.map(r -> r.removedKm().kilometers()).orElse(null),
                part.kmDone(currentMileage).orElse(null));
    }
}
