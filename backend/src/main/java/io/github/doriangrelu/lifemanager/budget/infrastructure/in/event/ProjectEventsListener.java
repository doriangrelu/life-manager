package io.github.doriangrelu.lifemanager.budget.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.budget.application.port.in.ForgetProject;
import io.github.doriangrelu.lifemanager.budget.application.port.in.TrackProject;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.ForgetProjectCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.TrackProjectCommand;
import io.github.doriangrelu.lifemanager.project.contract.event.ProjectChangedV1;
import io.github.doriangrelu.lifemanager.project.contract.event.ProjectDeletedV1;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/** Écoute les événements publics des projets : le budget sait à quels projets rattacher une dépense. */
@Singleton
class ProjectEventsListener {

    private final TrackProject trackProject;
    private final ForgetProject forgetProject;

    ProjectEventsListener(TrackProject trackProject, ForgetProject forgetProject) {
        this.trackProject = trackProject;
        this.forgetProject = forgetProject;
    }

    @EventListener
    void on(ProjectChangedV1 event) {
        trackProject.track(new TrackProjectCommand(event.projectId(), event.householdId(), event.name(),
                event.status(), event.countsInBudget()));
    }

    @EventListener
    void on(ProjectDeletedV1 event) {
        forgetProject.forget(new ForgetProjectCommand(event.projectId()));
    }
}
