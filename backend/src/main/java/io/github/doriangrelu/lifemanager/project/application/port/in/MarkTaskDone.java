package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.MarkTaskDoneCommand;

/** Coche ou décoche une tâche. */
public interface MarkTaskDone {

    void mark(MarkTaskDoneCommand command);
}
