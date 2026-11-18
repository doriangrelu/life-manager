package io.github.doriangrelu.lifemanager.bills.infrastructure.in.schedule;

import io.github.doriangrelu.lifemanager.bills.application.port.in.AdvanceChargeSchedules;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

/**
 * Fait avancer les calendriers des charges chaque matin à 6 h (heure des utilisateurs),
 * et au démarrage pour rattraper un arrêt du serveur.
 */
@Singleton
class ChargeScheduleTrigger {

    private final AdvanceChargeSchedules advanceSchedules;

    ChargeScheduleTrigger(AdvanceChargeSchedules advanceSchedules) {
        this.advanceSchedules = advanceSchedules;
    }

    @Scheduled(cron = "0 0 6 * * *", zoneId = "${life-manager.time-zone}")
    void everyMorning() {
        advanceSchedules.advanceAll();
    }

    @EventListener
    void onStartup(ServerStartupEvent event) {
        advanceSchedules.advanceAll();
    }
}
