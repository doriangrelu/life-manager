package io.github.doriangrelu.lifemanager.project.application.port.out;

import io.github.doriangrelu.lifemanager.project.domain.aggregate.Project;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectId;

import java.util.List;
import java.util.Optional;

/** Stockage des projets. */
public interface ProjectRepository {

    Optional<Project> findById(ProjectId id);

    /** Projets du foyer, les plus récents d'abord. */
    List<Project> findAllByHousehold(HouseholdId householdId);

    void save(Project project);

    void delete(ProjectId id);
}
