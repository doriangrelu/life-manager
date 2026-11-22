package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectRef;

import java.util.Optional;
import java.util.Set;

/** Copie locale des projets de chaque foyer (alimentée par les événements du domaine project). */
public interface ProjectRefs {

    /** Le projet, s'il est connu et appartient à ce foyer. */
    Optional<ProjectRef> find(HouseholdId householdId, ProjectId projectId);

    /** Projets du foyer dont les dépenses ne comptent pas dans le budget. */
    Set<ProjectId> offBudget(HouseholdId householdId);

    /** Ajoute ou remplace le projet. */
    void save(ProjectRef project);

    /** Oublie le projet ; ses dépenses n'ont plus de projet. */
    void forget(ProjectId projectId);
}
