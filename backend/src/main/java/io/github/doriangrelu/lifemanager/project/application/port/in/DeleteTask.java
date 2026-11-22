package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.TaskCommand;

/** Supprime une tâche. */
public interface DeleteTask {

    void delete(TaskCommand command);
}
