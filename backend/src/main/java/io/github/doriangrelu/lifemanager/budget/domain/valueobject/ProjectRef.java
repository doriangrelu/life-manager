package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/**
 * Ce que le budget sait d'un projet (copie tenue par les événements du domaine project).
 *
 * @param active         vrai si le projet est en cours (on peut y rattacher une dépense)
 * @param countsInBudget vrai si ses dépenses comptent dans le budget du mois
 */
public record ProjectRef(ProjectId id, HouseholdId householdId, String name, boolean active, boolean countsInBudget) {

    public ProjectRef {
        Require.notNull(id, "Le projet");
        Require.notNull(householdId, "Le foyer du projet");
        Require.notNull(name, "Le nom du projet");
    }
}
