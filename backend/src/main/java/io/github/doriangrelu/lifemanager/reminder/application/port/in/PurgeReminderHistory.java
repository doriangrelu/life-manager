package io.github.doriangrelu.lifemanager.reminder.application.port.in;

/** Efface les lignes d'historique faites il y a plus d'un an (tous foyers). */
public interface PurgeReminderHistory {

    void purge();
}
