package io.github.doriangrelu.lifemanager.project.domain.aggregate;

import io.github.doriangrelu.lifemanager.project.domain.event.TaskDeadlineChanged;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskDetails;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskState;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Tâche d'un projet. Une tâche datée, pas faite, d'un projet en cours est rappelée au foyer :
 * chaque changement annonce sa date à rappeler (ou plus rien).
 */
public final class ProjectTask extends AggregateRoot<TaskId> {

    private final ProjectId projectId;
    private final HouseholdId householdId;
    private TaskDetails details;
    private Instant doneAt;
    private int position;

    private ProjectTask(TaskId id, ProjectId projectId, HouseholdId householdId, TaskDetails details,
                        TaskState state) {
        super(id);
        this.projectId = Require.notNull(projectId, "Le projet de la tâche");
        this.householdId = Require.notNull(householdId, "Le foyer de la tâche");
        this.details = Require.notNull(details, "Le détail de la tâche");
        Require.notNull(state, "L'état de la tâche");
        this.doneAt = state.doneAt();
        this.position = state.position();
    }

    /** Nouvelle tâche, placée à la position donnée (en fin de liste). */
    public static ProjectTask create(Project project, TaskDetails details, int position) {
        return new ProjectTask(TaskId.newId(), project.id(), project.householdId(), details,
                new TaskState(null, position));
    }

    /** Recharge une tâche depuis le stockage. */
    public static ProjectTask restore(TaskId id, ProjectId projectId, HouseholdId householdId, TaskDetails details,
                                      TaskState state) {
        return new ProjectTask(id, projectId, householdId, details, state);
    }

    public void update(TaskDetails newDetails) {
        details = Require.notNull(newDetails, "Le détail de la tâche");
    }

    /** Coche (fait) ou décoche la tâche ; cocher deux fois garde la première date. */
    public void markDone(boolean done, Instant now) {
        if (!done) {
            doneAt = null;
        } else if (doneAt == null) {
            doneAt = now;
        }
    }

    public void moveTo(int newPosition) {
        position = newPosition;
    }

    /** Annonce la date à rappeler selon l'état de la tâche et de son projet. */
    public void announce(Project project, Instant now) {
        LocalDate dueOn = isDone() || !project.details().isActive() ? null : details.dueOn();
        recordEvent(new TaskDeadlineChanged(id(), householdId, project.details().name().value(),
                details.title().value(), dueOn, now));
    }

    /** La tâche va disparaître : plus rien à rappeler. */
    public void forget(Project project, Instant now) {
        recordEvent(new TaskDeadlineChanged(id(), householdId, project.details().name().value(),
                details.title().value(), null, now));
    }

    public boolean belongsTo(ProjectId project) {
        return projectId.equals(project);
    }

    public boolean isDone() {
        return doneAt != null;
    }

    public ProjectId projectId() {
        return projectId;
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public TaskDetails details() {
        return details;
    }

    public Instant doneAt() {
        return doneAt;
    }

    public int position() {
        return position;
    }
}
