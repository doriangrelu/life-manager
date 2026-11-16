package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.ListReminders;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.query.ListRemindersQuery;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.ReminderResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;
import java.util.UUID;

/** Rappels d'un foyer : échéances passées ou proches. */
@Controller("/api/households/{householdId}/reminders")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ReminderController {

    private final ListReminders listReminders;

    ReminderController(ListReminders listReminders) {
        this.listReminders = listReminders;
    }

    @Get
    List<ReminderResponse> list(Authentication user, UUID householdId) {
        return listReminders.list(new ListRemindersQuery(user.getName(), householdId)).stream()
                .map(ReminderResponse::from)
                .toList();
    }
}
