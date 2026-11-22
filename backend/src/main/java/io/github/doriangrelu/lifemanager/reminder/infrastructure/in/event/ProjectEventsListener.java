package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.project.contract.event.TaskDeadlineChangedV1;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.TrackTaskDeadline;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.TrackTaskDeadlineCommand;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/** Écoute les événements publics des projets : les rappels suivent les tâches datées. */
@Singleton
class ProjectEventsListener {

    private final TrackTaskDeadline trackDeadline;

    ProjectEventsListener(TrackTaskDeadline trackDeadline) {
        this.trackDeadline = trackDeadline;
    }

    @EventListener
    void on(TaskDeadlineChangedV1 event) {
        trackDeadline.track(new TrackTaskDeadlineCommand(event.taskId(), event.householdId(), event.projectName(),
                event.title(), event.dueOn()));
    }
}
