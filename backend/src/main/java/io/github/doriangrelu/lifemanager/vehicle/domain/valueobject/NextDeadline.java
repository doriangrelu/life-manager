package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Prochaine échéance fixée par une opération du carnet (contrôle technique, révision).
 *
 * @param kind  type d'opération qui porte l'échéance
 * @param dueOn date de la prochaine opération de ce type
 */
public record NextDeadline(MaintenanceKind kind, LocalDate dueOn) {

    public NextDeadline {
        Require.notNull(kind, "Le type d'entretien");
        Require.notNull(dueOn, "La prochaine échéance");
    }

    /**
     * Échéance fixée par une opération : la date demandée, ou à défaut la date de l'opération plus la durée du type.
     *
     * @param requested date précise choisie par le membre, ou {@code null} pour le calcul automatique
     * @return vide si le type ne fixe pas d'échéance
     * @throws InvalidValueException si une date est demandée pour un type sans échéance, ou n'est pas postérieure
     *                               à l'opération
     */
    public static Optional<NextDeadline> after(MaintenanceDetails details, LocalDate requested) {
        var validity = details.kind().validity();
        if (validity.isEmpty()) {
            if (requested != null) {
                throw new InvalidValueException("Ce type d'opération ne fixe pas de prochaine échéance");
            }
            return Optional.empty();
        }
        if (requested != null && !requested.isAfter(details.performedOn())) {
            throw new InvalidValueException("La prochaine échéance doit suivre la date de l'opération");
        }
        var dueOn = requested != null ? requested : details.performedOn().plus(validity.get());
        return Optional.of(new NextDeadline(details.kind(), dueOn));
    }
}
