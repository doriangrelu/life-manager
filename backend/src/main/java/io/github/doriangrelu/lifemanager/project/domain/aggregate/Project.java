package io.github.doriangrelu.lifemanager.project.domain.aggregate;

import io.github.doriangrelu.lifemanager.project.domain.event.ProjectChanged;
import io.github.doriangrelu.lifemanager.project.domain.event.ProjectDeleted;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectDetails;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Instant;

/** Projet du foyer (travaux, vacances…) : un budget prévu, des dépenses rattachées, des tâches. */
public final class Project extends AggregateRoot<ProjectId> {

    private final HouseholdId householdId;
    private final Instant createdAt;
    private ProjectDetails details;

    private Project(ProjectId id, HouseholdId householdId, ProjectDetails details, Instant createdAt) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer du projet");
        this.details = Require.notNull(details, "Le détail du projet");
        this.createdAt = Require.notNull(createdAt, "La date de création du projet");
    }

    /** Nouveau projet du foyer ; annoncé aux autres domaines. */
    public static Project create(HouseholdId householdId, ProjectDetails details, Instant now) {
        var project = new Project(ProjectId.newId(), householdId, details, now);
        project.recordEvent(new ProjectChanged(project.id(), householdId, details, now));
        return project;
    }

    /** Recharge un projet depuis le stockage. */
    public static Project restore(ProjectId id, HouseholdId householdId, ProjectDetails details, Instant createdAt) {
        return new Project(id, householdId, details, createdAt);
    }

    public void update(ProjectDetails newDetails, Instant now) {
        details = Require.notNull(newDetails, "Le détail du projet");
        recordEvent(new ProjectChanged(id(), householdId, details, now));
    }

    /** Suppression : les autres domaines oublient le projet. */
    public void delete(Instant now) {
        recordEvent(new ProjectDeleted(id(), householdId, now));
    }

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public ProjectDetails details() {
        return details;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
