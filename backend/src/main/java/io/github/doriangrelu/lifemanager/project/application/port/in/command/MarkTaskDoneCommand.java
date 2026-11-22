package io.github.doriangrelu.lifemanager.project.application.port.in.command;

/** Coche ({@code done}) ou décoche une tâche. */
public record MarkTaskDoneCommand(TaskCommand task, boolean done) {
}
