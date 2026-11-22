package io.github.doriangrelu.lifemanager.project.application.port.out;

import io.github.doriangrelu.lifemanager.project.domain.aggregate.ProjectTask;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskId;

import java.util.List;
import java.util.Optional;

/** Stockage des tâches de projet. */
public interface TaskRepository {

    Optional<ProjectTask> findById(TaskId id);

    /** Tâches du projet, dans l'ordre choisi par le foyer. */
    List<ProjectTask> findAllByProject(ProjectId projectId);

    /** Toutes les tâches du foyer (pour l'avancement de chaque projet). */
    List<ProjectTask> findAllByHousehold(HouseholdId householdId);

    /** Position à donner à une nouvelle tâche : après la dernière. */
    int nextPosition(ProjectId projectId);

    void save(ProjectTask task);

    void delete(TaskId id);

    void deleteAllByProject(ProjectId projectId);
}
