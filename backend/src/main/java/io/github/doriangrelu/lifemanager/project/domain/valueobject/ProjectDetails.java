package io.github.doriangrelu.lifemanager.project.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;

/**
 * Ce qui décrit un projet. Seuls le nom, la couleur, l'état et l'option budget sont obligatoires.
 *
 * @param plannedCents   budget prévu en centimes, ou {@code null}
 * @param countsInBudget vrai si ses dépenses comptent dans le budget du mois
 */
public record ProjectDetails(ProjectName name, String description, ProjectColor color, Long plannedCents,
                             LocalDate startOn, LocalDate targetOn, ProjectStatus status, boolean countsInBudget) {

    private static final int DESCRIPTION_MAX = 500;

    public ProjectDetails {
        Require.notNull(name, "Le nom du projet");
        Require.notNull(color, "La couleur du projet");
        Require.notNull(status, "L'état du projet");
        description = description == null || description.isBlank() ? null
                : Require.maxLength(description.strip(), DESCRIPTION_MAX, "La description du projet");
        if (plannedCents != null && plannedCents < 0) {
            throw new InvalidValueException("Le budget prévu ne peut pas être négatif");
        }
        if (startOn != null && targetOn != null && targetOn.isBefore(startOn)) {
            throw new InvalidValueException("La fin visée ne peut pas précéder le début");
        }
    }

    public boolean isActive() {
        return status == ProjectStatus.ACTIVE;
    }
}
