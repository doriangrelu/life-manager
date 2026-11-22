package io.github.doriangrelu.lifemanager.vehicle.application.port.in.result;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Cost;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceNote;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceOtherLabel;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Mileage;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une opération du carnet telle que vue hors du domaine ; coût en centimes, champs absents à {@code null}.
 *
 * @param attachmentCount nombre de pièces jointes de l'opération
 * @param otherLabel      précision du type « Autre », ou {@code null}
 * @param inBudget        son coût a été ajouté au budget
 */
public record MaintenanceEntryView(UUID id, LocalDate performedOn, String kind, Integer mileageKm, Long costCents,
                                   String note, long attachmentCount, String otherLabel, boolean inBudget) {

    public static MaintenanceEntryView from(MaintenanceEntry entry, long attachmentCount, boolean inBudget) {
        var details = entry.details();
        return new MaintenanceEntryView(
                entry.id().value(),
                details.performedOn(),
                details.kind().name(),
                details.optionalMileage().map(Mileage::kilometers).orElse(null),
                details.optionalCost().map(Cost::cents).orElse(null),
                details.optionalNote().map(MaintenanceNote::value).orElse(null),
                attachmentCount,
                details.optionalOtherLabel().map(MaintenanceOtherLabel::value).orElse(null),
                inBudget);
    }
}
