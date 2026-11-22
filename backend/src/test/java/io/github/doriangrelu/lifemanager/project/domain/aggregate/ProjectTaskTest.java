package io.github.doriangrelu.lifemanager.project.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.project.domain.event.TaskDeadlineChanged;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectColor;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectDetails;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectName;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectStatus;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskDetails;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskTitle;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectTaskTest {

    private static final Instant NOW = Instant.parse("2026-11-22T10:00:00Z");
    private static final LocalDate DUE = LocalDate.of(2026, 12, 15);

    @Test
    void a_dated_open_task_of_an_active_project_is_announced_with_its_date() {
        var project = project(ProjectStatus.ACTIVE);
        var task = ProjectTask.create(project, new TaskDetails(new TaskTitle("Carrelage"), null, DUE), 0);

        assertThat(announced(project, task).dueOn()).isEqualTo(DUE);
    }

    @Test
    void a_done_task_or_a_closed_project_has_nothing_to_remind() {
        var project = project(ProjectStatus.ACTIVE);
        var task = ProjectTask.create(project, new TaskDetails(new TaskTitle("Carrelage"), null, DUE), 0);
        task.markDone(true, NOW);
        assertThat(announced(project, task).dueOn()).isNull();

        task.markDone(false, NOW);
        assertThat(announced(project(ProjectStatus.ABANDONED), task).dueOn()).isNull();
    }

    @Test
    void checking_twice_keeps_the_first_date() {
        var task = ProjectTask.create(project(ProjectStatus.ACTIVE), new TaskDetails(new TaskTitle("A"), null, null), 0);
        task.markDone(true, NOW);
        task.markDone(true, NOW.plusSeconds(60));
        assertThat(task.doneAt()).isEqualTo(NOW);
    }

    @Test
    void a_project_cannot_end_before_it_starts_nor_have_a_negative_budget() {
        assertThatThrownBy(() -> new ProjectDetails(new ProjectName("X"), null, ProjectColor.TEAL, null,
                DUE, DUE.minusDays(1), ProjectStatus.ACTIVE, true)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new ProjectDetails(new ProjectName("X"), null, ProjectColor.TEAL, -1L,
                null, null, ProjectStatus.ACTIVE, true)).isInstanceOf(InvalidValueException.class);
    }

    private static TaskDeadlineChanged announced(Project project, ProjectTask task) {
        task.pullEvents();
        task.announce(project, NOW);
        return (TaskDeadlineChanged) task.pullEvents().getFirst();
    }

    private static Project project(ProjectStatus status) {
        return Project.create(new HouseholdId(UUID.randomUUID()), new ProjectDetails(new ProjectName("Salle de bain"),
                null, ProjectColor.TEAL, null, null, null, status, true), NOW);
    }
}
