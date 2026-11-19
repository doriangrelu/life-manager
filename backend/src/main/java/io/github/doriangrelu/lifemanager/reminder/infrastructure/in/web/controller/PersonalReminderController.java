package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.AddPersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.CompletePersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.DeletePersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.ListPersonalReminders;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.UpdatePersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.AddPersonalReminderCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.PersonalReminderCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.UpdatePersonalReminderCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.query.ListRemindersQuery;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.PersonalReminderRequest;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.PersonalReminderResponse;
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

/** Rappels saisis par les membres : liste, ajout, modification, suppression, « c'est fait ». */
@Controller("/api/households/{householdId}/personal-reminders")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class PersonalReminderController {

    private final ListPersonalReminders listReminders;
    private final AddPersonalReminder addReminder;
    private final UpdatePersonalReminder updateReminder;
    private final DeletePersonalReminder deleteReminder;
    private final CompletePersonalReminder completeReminder;

    PersonalReminderController(ListPersonalReminders listReminders, AddPersonalReminder addReminder,
                               UpdatePersonalReminder updateReminder, DeletePersonalReminder deleteReminder,
                               CompletePersonalReminder completeReminder) {
        this.listReminders = listReminders;
        this.addReminder = addReminder;
        this.updateReminder = updateReminder;
        this.deleteReminder = deleteReminder;
        this.completeReminder = completeReminder;
    }

    @Get
    List<PersonalReminderResponse> list(Authentication user, UUID householdId) {
        return listReminders.list(new ListRemindersQuery(user.getName(), householdId)).stream()
                .map(PersonalReminderResponse::from)
                .toList();
    }

    @Post
    HttpResponse<PersonalReminderResponse> add(Authentication user, UUID householdId,
                                               @Body @Valid PersonalReminderRequest request) {
        var added = addReminder.add(new AddPersonalReminderCommand(user.getName(), householdId, request.toInput()));
        return HttpResponse.created(PersonalReminderResponse.from(added));
    }

    @Put("/{reminderId}")
    PersonalReminderResponse update(Authentication user, UUID householdId, UUID reminderId,
                                    @Body @Valid PersonalReminderRequest request) {
        var command = new UpdatePersonalReminderCommand(user.getName(), householdId, reminderId, request.toInput());
        return PersonalReminderResponse.from(updateReminder.update(command));
    }

    @Delete("/{reminderId}")
    HttpResponse<Void> delete(Authentication user, UUID householdId, UUID reminderId) {
        deleteReminder.delete(new PersonalReminderCommand(user.getName(), householdId, reminderId));
        return HttpResponse.noContent();
    }

    @Post("/{reminderId}/done")
    HttpResponse<Void> done(Authentication user, UUID householdId, UUID reminderId) {
        completeReminder.complete(new PersonalReminderCommand(user.getName(), householdId, reminderId));
        return HttpResponse.noContent();
    }
}
