package io.github.doriangrelu.lifemanager.bills.application.service;

import io.github.doriangrelu.lifemanager.bills.application.port.out.ChargeRepository;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Fait avancer une charge dans sa propre transaction : l'échec d'une charge n'empêche pas les autres. */
@Singleton
public class ScheduleRunner {

    private final ChargeRepository charges;
    private final ChargeScheduler scheduler;

    ScheduleRunner(ChargeRepository charges, ChargeScheduler scheduler) {
        this.charges = charges;
        this.scheduler = scheduler;
    }

    @Transactional(transactionManager = "bills")
    public void advance(ChargeId chargeId) {
        charges.findById(chargeId).ifPresent(scheduler::advance);
    }
}
