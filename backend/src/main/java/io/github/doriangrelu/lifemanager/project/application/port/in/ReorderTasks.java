package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.ReorderTasksCommand;

/** Change l'ordre des tâches d'un projet. */
public interface ReorderTasks {

    void reorder(ReorderTasksCommand command);
}
