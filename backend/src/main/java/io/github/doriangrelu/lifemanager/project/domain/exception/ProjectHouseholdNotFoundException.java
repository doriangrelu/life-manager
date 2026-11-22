package io.github.doriangrelu.lifemanager.project.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Foyer inconnu du domaine projets, ou dont l'utilisateur n'est pas membre (même erreur dans les deux cas). */
public class ProjectHouseholdNotFoundException extends NotFoundException {

    public ProjectHouseholdNotFoundException() {
        super("Foyer introuvable");
    }
}
