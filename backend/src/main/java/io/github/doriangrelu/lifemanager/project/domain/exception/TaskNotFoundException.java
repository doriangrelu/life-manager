package io.github.doriangrelu.lifemanager.project.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Tâche inconnue, ou d'un autre projet (même erreur dans les deux cas). */
public class TaskNotFoundException extends NotFoundException {

    public TaskNotFoundException() {
        super("Tâche introuvable");
    }
}
