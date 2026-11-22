package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.AddTaskCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.TaskView;

/** Ajoute une tâche à un projet. */
public interface AddTask {

    TaskView add(AddTaskCommand command);
}
