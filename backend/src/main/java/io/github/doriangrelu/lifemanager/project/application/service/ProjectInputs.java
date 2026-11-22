package io.github.doriangrelu.lifemanager.project.application.service;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.ProjectInput;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.TaskInput;
import io.github.doriangrelu.lifemanager.project.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectColor;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectDetails;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectName;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectStatus;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskDetails;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskTitle;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import jakarta.inject.Singleton;

/** Transforme les saisies en objets du domaine ; le responsable d'une tâche doit être membre du foyer. */
@Singleton
public class ProjectInputs {

    private final HouseholdMembers members;

    ProjectInputs(HouseholdMembers members) {
        this.members = members;
    }

    public static ProjectDetails details(ProjectInput input) {
        return new ProjectDetails(new ProjectName(input.name()), input.description(), ProjectColor.of(input.color()),
                input.plannedCents(), input.startOn(), input.targetOn(), ProjectStatus.of(input.status()),
                input.countsInBudget());
    }

    public TaskDetails task(HouseholdId household, TaskInput input) {
        MemberId assignee = null;
        if (input.assigneeId() != null) {
            assignee = new MemberId(input.assigneeId());
            if (!members.isMember(household, assignee)) {
                throw new InvalidValueException("Le responsable de la tâche doit être membre du foyer");
            }
        }
        return new TaskDetails(new TaskTitle(input.title()), assignee, input.dueOn());
    }
}
