package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.ListReminderHistory;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.RemoveReminderDone;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.RemoveReminderDoneCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.query.ReminderHistoryQuery;
import io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto.ReminderDoneResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;
import java.util.UUID;

/** Historique des rappels faits : une page (20 lignes à partir de {@code offset}), retrait d'une ligne. */
@Controller("/api/households/{householdId}/reminder-history")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ReminderHistoryController {

    private final ListReminderHistory listHistory;
    private final RemoveReminderDone removeEntry;

    ReminderHistoryController(ListReminderHistory listHistory, RemoveReminderDone removeEntry) {
        this.listHistory = listHistory;
        this.removeEntry = removeEntry;
    }

    @Get
    List<ReminderDoneResponse> list(Authentication user, UUID householdId,
                                    @QueryValue(defaultValue = "0") int offset) {
        return listHistory.list(new ReminderHistoryQuery(user.getName(), householdId, offset)).stream()
                .map(ReminderDoneResponse::from)
                .toList();
    }

    @Delete("/{entryId}")
    HttpResponse<Void> remove(Authentication user, UUID householdId, UUID entryId) {
        removeEntry.remove(new RemoveReminderDoneCommand(user.getName(), householdId, entryId));
        return HttpResponse.noContent();
    }
}
