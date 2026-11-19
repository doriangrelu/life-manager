package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.ListReminderHistory;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.query.ReminderHistoryQuery;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.ReminderDoneView;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.ReminderDoneRepository;
import io.github.doriangrelu.lifemanager.reminder.application.service.ReminderAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Donne à un membre une page de l'historique des rappels faits de son foyer. */
@Singleton
class ListReminderHistoryUseCase implements ListReminderHistory {

    /** Taille d'une page : la PWA propose « Voir plus » quand elle reçoit une page pleine. */
    static final int PAGE_SIZE = 20;

    private final ReminderAccess access;
    private final ReminderDoneRepository history;

    ListReminderHistoryUseCase(ReminderAccess access, ReminderDoneRepository history) {
        this.access = access;
        this.history = history;
    }

    @Override
    @Transactional(transactionManager = "reminder", readOnly = true)
    public List<ReminderDoneView> list(ReminderHistoryQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        return history.findPage(household, query.offset(), PAGE_SIZE).stream().map(ReminderDoneView::from).toList();
    }
}
