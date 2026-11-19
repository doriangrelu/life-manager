package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.schedule;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.PurgeReminderHistory;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

/** Ménage de l'historique des rappels chaque nuit à 4 h 45 (heure des utilisateurs). */
@Singleton
class ReminderHistoryPurgeTrigger {

    private final PurgeReminderHistory purgeHistory;

    ReminderHistoryPurgeTrigger(PurgeReminderHistory purgeHistory) {
        this.purgeHistory = purgeHistory;
    }

    @Scheduled(cron = "0 45 4 * * *", zoneId = "${life-manager.time-zone}")
    void everyNight() {
        purgeHistory.purge();
    }
}
