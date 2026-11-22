package io.github.doriangrelu.lifemanager.project.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Projet inconnu, ou d'un autre foyer (même erreur dans les deux cas). */
public class ProjectNotFoundException extends NotFoundException {

    public ProjectNotFoundException() {
        super("Projet introuvable");
    }
}
