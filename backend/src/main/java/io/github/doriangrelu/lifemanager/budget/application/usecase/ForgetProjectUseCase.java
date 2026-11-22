package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.ForgetProject;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.ForgetProjectCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ProjectRefs;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Projet supprimé : ses dépenses restent, sans projet, et comptent de nouveau dans le budget. */
@Singleton
class ForgetProjectUseCase implements ForgetProject {

    private final ProjectRefs projects;

    ForgetProjectUseCase(ProjectRefs projects) {
        this.projects = projects;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public void forget(ForgetProjectCommand command) {
        projects.forget(new ProjectId(command.projectId()));
    }
}
