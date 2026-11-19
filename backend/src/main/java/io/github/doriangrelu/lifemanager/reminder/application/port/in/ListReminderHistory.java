package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.query.ReminderHistoryQuery;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.ReminderDoneView;

import java.util.List;

/** Historique des rappels faits du foyer, par pages, du plus récent au plus ancien. */
public interface ListReminderHistory {

    List<ReminderDoneView> list(ReminderHistoryQuery query);
}
