package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.out.ProjectRefs;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectRef;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.ExpenseJdbcRepository;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.ProjectRefJdbcRepository;
import jakarta.inject.Singleton;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Implémente la copie locale des projets avec PostgreSQL (table project_ref). */
@Singleton
class ProjectRefsAdapter implements ProjectRefs {

    private static final String ACTIVE = "ACTIVE";

    private final ProjectRefJdbcRepository table;
    private final ExpenseJdbcRepository expenses;

    ProjectRefsAdapter(ProjectRefJdbcRepository table, ExpenseJdbcRepository expenses) {
        this.table = table;
        this.expenses = expenses;
    }

    @Override
    public Optional<ProjectRef> find(HouseholdId householdId, ProjectId projectId) {
        return table.findById(projectId.value())
                .filter(row -> row.householdId().equals(householdId.value()))
                .map(row -> new ProjectRef(projectId, householdId, row.name(), ACTIVE.equals(row.status()),
                        row.countsInBudget()));
    }

    @Override
    public Set<ProjectId> offBudget(HouseholdId householdId) {
        return table.findOffBudget(householdId.value()).stream().map(ProjectId::new).collect(Collectors.toSet());
    }

    @Override
    public void save(ProjectRef project) {
        table.upsert(project.id().value(), project.householdId().value(), project.name(),
                project.active() ? ACTIVE : "CLOSED", project.countsInBudget());
    }

    @Override
    public void forget(ProjectId projectId) {
        expenses.detachProject(projectId.value());
        table.deleteById(projectId.value());
    }
}
