package io.github.doriangrelu.lifemanager.budget.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Projet inconnu du budget, ou d'un autre foyer (même erreur dans les deux cas). */
public class ProjectNotFoundException extends NotFoundException {

    public ProjectNotFoundException() {
        super("Projet introuvable");
    }
}
