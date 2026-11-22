package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.TrackProject;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.TrackProjectCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ProjectRefs;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectRef;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Mémorise un projet (nom, état, option budget) dans la copie locale du budget. */
@Singleton
class TrackProjectUseCase implements TrackProject {

    private final ProjectRefs projects;
    private final HouseholdMembers members;

    TrackProjectUseCase(ProjectRefs projects, HouseholdMembers members) {
        this.projects = projects;
        this.members = members;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public void track(TrackProjectCommand command) {
        var household = new HouseholdId(command.householdId());
        // Foyer supprimé entre-temps (événement en retard) : rien à garder.
        if (!members.isKnown(household)) {
            return;
        }
        projects.save(new ProjectRef(new ProjectId(command.projectId()), household, command.name(),
                "ACTIVE".equals(command.status()), command.countsInBudget()));
    }
}
