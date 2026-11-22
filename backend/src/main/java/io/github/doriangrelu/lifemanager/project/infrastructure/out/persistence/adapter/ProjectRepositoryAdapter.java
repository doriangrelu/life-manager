package io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.project.application.port.out.ProjectRepository;
import io.github.doriangrelu.lifemanager.project.domain.aggregate.Project;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectColor;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectDetails;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectName;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectStatus;
import io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.entity.ProjectEntity;
import io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.repository.ProjectJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente le stockage des projets avec PostgreSQL (table project). */
@Singleton
class ProjectRepositoryAdapter implements ProjectRepository {

    private final ProjectJdbcRepository table;

    ProjectRepositoryAdapter(ProjectJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
        return table.findById(id.value()).map(ProjectRepositoryAdapter::toDomain);
    }

    @Override
    public List<Project> findAllByHousehold(HouseholdId householdId) {
        return table.findAllByHousehold(householdId.value()).stream().map(ProjectRepositoryAdapter::toDomain).toList();
    }

    @Override
    public void save(Project project) {
        var entity = toEntity(project);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void delete(ProjectId id) {
        table.deleteById(id.value());
    }

    private static ProjectEntity toEntity(Project project) {
        var d = project.details();
        return new ProjectEntity(project.id().value(), project.householdId().value(), d.name().value(),
                d.description(), d.color().name(), d.plannedCents(), d.startOn(), d.targetOn(), d.status().name(),
                d.countsInBudget(), project.createdAt());
    }

    private static Project toDomain(ProjectEntity e) {
        var details = new ProjectDetails(new ProjectName(e.name()), e.description(), ProjectColor.of(e.color()),
                e.plannedCents(), e.startOn(), e.targetOn(), ProjectStatus.of(e.status()), e.countsInBudget());
        return Project.restore(new ProjectId(e.id()), new HouseholdId(e.householdId()), details, e.createdAt());
    }
}
