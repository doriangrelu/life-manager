package io.github.doriangrelu.lifemanager.project.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.project.application.port.in.AddProject;
import io.github.doriangrelu.lifemanager.project.application.port.in.DeleteProject;
import io.github.doriangrelu.lifemanager.project.application.port.in.GetProject;
import io.github.doriangrelu.lifemanager.project.application.port.in.ListProjects;
import io.github.doriangrelu.lifemanager.project.application.port.in.UpdateProject;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.AddProjectCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.ProjectCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.UpdateProjectCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.query.ListProjectsQuery;
import io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto.ProjectDetailsResponse;
import io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto.ProjectRequest;
import io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto.ProjectResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

/** Projets du foyer : liste, fiche, ajout, modification, suppression. */
@Controller("/api/households/{householdId}/projects")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ProjectController {

    private final ListProjects listProjects;
    private final GetProject getProject;
    private final AddProject addProject;
    private final UpdateProject updateProject;
    private final DeleteProject deleteProject;

    ProjectController(ListProjects listProjects, GetProject getProject, AddProject addProject,
                      UpdateProject updateProject, DeleteProject deleteProject) {
        this.listProjects = listProjects;
        this.getProject = getProject;
        this.addProject = addProject;
        this.updateProject = updateProject;
        this.deleteProject = deleteProject;
    }

    @Get
    List<ProjectResponse> list(Authentication user, UUID householdId) {
        return listProjects.list(new ListProjectsQuery(user.getName(), householdId)).stream()
                .map(ProjectResponse::from).toList();
    }

    @Get("/{projectId}")
    ProjectDetailsResponse get(Authentication user, UUID householdId, UUID projectId) {
        return ProjectDetailsResponse.from(getProject.get(new ProjectCommand(user.getName(), householdId, projectId)));
    }

    @Post
    HttpResponse<ProjectResponse> add(Authentication user, UUID householdId, @Body @Valid ProjectRequest request) {
        var added = addProject.add(new AddProjectCommand(user.getName(), householdId, request.toInput()));
        return HttpResponse.created(ProjectResponse.from(added));
    }

    @Put("/{projectId}")
    ProjectResponse update(Authentication user, UUID householdId, UUID projectId,
                           @Body @Valid ProjectRequest request) {
        var command = new UpdateProjectCommand(user.getName(), householdId, projectId, request.toInput());
        return ProjectResponse.from(updateProject.update(command));
    }

    @Delete("/{projectId}")
    HttpResponse<Void> delete(Authentication user, UUID householdId, UUID projectId) {
        deleteProject.delete(new ProjectCommand(user.getName(), householdId, projectId));
        return HttpResponse.noContent();
    }
}
