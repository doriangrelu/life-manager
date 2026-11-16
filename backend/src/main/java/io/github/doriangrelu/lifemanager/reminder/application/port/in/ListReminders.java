package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.query.ListRemindersQuery;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.ReminderView;

import java.util.List;
/** Rappels d'un foyer : échéances passées ou proches, de la plus ancienne à la plus lointaine. */
public interface ListReminders {

    List<ReminderView> list(ListRemindersQuery query);
}
