package io.github.doriangrelu.lifemanager.project.application.service;

import io.github.doriangrelu.lifemanager.project.domain.aggregate.Project;
import io.github.doriangrelu.lifemanager.project.domain.aggregate.ProjectTask;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/** Publie les événements du domaine ; annonce aux rappels la date de chaque tâche (ou plus rien). */
@Singleton
public class TaskAnnouncer {

    private final DomainEventPublisher events;
    private final Clock clock;

    TaskAnnouncer(DomainEventPublisher events, Clock clock) {
        this.events = events;
        this.clock = clock;
    }

    public Instant now() {
        return clock.instant();
    }

    public void publish(AggregateRoot<?> aggregate) {
        events.publishEventsOf(aggregate);
    }

    public void announce(Project project, List<ProjectTask> tasks) {
        var now = clock.instant();
        tasks.forEach(task -> {
            task.announce(project, now);
            events.publishEventsOf(task);
        });
    }

    public void forget(Project project, List<ProjectTask> tasks) {
        var now = clock.instant();
        tasks.forEach(task -> {
            task.forget(project, now);
            events.publishEventsOf(task);
        });
    }
}
