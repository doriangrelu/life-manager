package io.github.doriangrelu.lifemanager.project.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.project.application.port.in.AddTask;
import io.github.doriangrelu.lifemanager.project.application.port.in.DeleteTask;
import io.github.doriangrelu.lifemanager.project.application.port.in.MarkTaskDone;
import io.github.doriangrelu.lifemanager.project.application.port.in.ReorderTasks;
import io.github.doriangrelu.lifemanager.project.application.port.in.UpdateTask;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.AddTaskCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.MarkTaskDoneCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.ReorderTasksCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.TaskCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.UpdateTaskCommand;
import io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto.TaskOrderRequest;
import io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto.TaskRequest;
import io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto.TaskResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.UUID;

/** Tâches d'un projet : ajout, modification, suppression, cocher, ordre. */
@Controller("/api/households/{householdId}/projects/{projectId}/tasks")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ProjectTaskController {

    private final AddTask addTask;
    private final UpdateTask updateTask;
    private final DeleteTask deleteTask;
    private final MarkTaskDone markDone;
    private final ReorderTasks reorderTasks;

    ProjectTaskController(AddTask addTask, UpdateTask updateTask, DeleteTask deleteTask, MarkTaskDone markDone,
                          ReorderTasks reorderTasks) {
        this.addTask = addTask;
        this.updateTask = updateTask;
        this.deleteTask = deleteTask;
        this.markDone = markDone;
        this.reorderTasks = reorderTasks;
    }

    @Post
    HttpResponse<TaskResponse> add(Authentication user, UUID householdId, UUID projectId,
                                   @Body @Valid TaskRequest request) {
        var added = addTask.add(new AddTaskCommand(user.getName(), householdId, projectId, request.toInput()));
        return HttpResponse.created(TaskResponse.from(added));
    }

    @Put("/order")
    HttpResponse<Void> reorder(Authentication user, UUID householdId, UUID projectId,
                               @Body @Valid TaskOrderRequest request) {
        reorderTasks.reorder(new ReorderTasksCommand(user.getName(), householdId, projectId, request.ids()));
        return HttpResponse.noContent();
    }

    @Put("/{taskId}")
    TaskResponse update(Authentication user, UUID householdId, UUID projectId, UUID taskId,
                        @Body @Valid TaskRequest request) {
        var command = new UpdateTaskCommand(user.getName(), householdId, projectId, taskId, request.toInput());
        return TaskResponse.from(updateTask.update(command));
    }

    @Delete("/{taskId}")
    HttpResponse<Void> delete(Authentication user, UUID householdId, UUID projectId, UUID taskId) {
        deleteTask.delete(new TaskCommand(user.getName(), householdId, projectId, taskId));
        return HttpResponse.noContent();
    }

    @Post("/{taskId}/done")
    HttpResponse<Void> done(Authentication user, UUID householdId, UUID projectId, UUID taskId) {
        markDone.mark(new MarkTaskDoneCommand(new TaskCommand(user.getName(), householdId, projectId, taskId), true));
        return HttpResponse.noContent();
    }

    @Delete("/{taskId}/done")
    HttpResponse<Void> undone(Authentication user, UUID householdId, UUID projectId, UUID taskId) {
        markDone.mark(new MarkTaskDoneCommand(new TaskCommand(user.getName(), householdId, projectId, taskId), false));
        return HttpResponse.noContent();
    }
}
