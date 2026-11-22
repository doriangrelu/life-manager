package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.AddProject;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.AddProjectCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectView;
import io.github.doriangrelu.lifemanager.project.application.port.out.ProjectRepository;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectHouseholdAccess;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectInputs;
import io.github.doriangrelu.lifemanager.project.domain.aggregate.Project;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.Authorship;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.UUID;

/** Ajoute un projet au foyer. */
@Singleton
class AddProjectUseCase implements AddProject {

    private final ProjectHouseholdAccess access;
    private final ProjectRepository projects;
    private final DomainEventPublisher events;
    private final Authorship authorship;
    private final Clock clock;

    AddProjectUseCase(ProjectHouseholdAccess access, ProjectRepository projects, DomainEventPublisher events,
                      @Named("project") Authorship authorship, Clock clock) {
        this.access = access;
        this.projects = projects;
        this.events = events;
        this.authorship = authorship;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "project")
    public ProjectView add(AddProjectCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var project = Project.create(household, ProjectInputs.details(command.input()), clock.instant());
        projects.save(project);
        authorship.record(project.id().value(), household.value(), UUID.fromString(command.requesterId()));
        events.publishEventsOf(project);
        return ProjectView.of(project, 0, 0);
    }
}
