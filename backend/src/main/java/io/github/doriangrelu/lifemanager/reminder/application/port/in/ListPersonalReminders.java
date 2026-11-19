package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.query.ListRemindersQuery;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.PersonalReminderView;

import java.util.List;

/** Rappels saisis du foyer, du plus proche au plus lointain. */
public interface ListPersonalReminders {

    List<PersonalReminderView> list(ListRemindersQuery query);
}
