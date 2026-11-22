package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.UpdateTaskCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.TaskView;

/** Modifie une tâche. */
public interface UpdateTask {

    TaskView update(UpdateTaskCommand command);
}
