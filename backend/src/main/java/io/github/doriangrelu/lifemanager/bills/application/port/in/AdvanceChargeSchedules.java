package io.github.doriangrelu.lifemanager.bills.application.port.in;


/** Fait avancer le calendrier de toutes les charges (tâche quotidienne et démarrage). */
public interface AdvanceChargeSchedules {

    void advanceAll();
}
