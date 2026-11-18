package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.TerminateCharge;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.out.ChargeRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeScheduler;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Résilie une charge : ses échéances à venir disparaissent, les passées restent consultables. */
@Singleton
class TerminateChargeUseCase implements TerminateCharge {

    private final ChargeAccess access;
    private final ChargeRepository charges;
    private final ChargeScheduler scheduler;

    TerminateChargeUseCase(ChargeAccess access, ChargeRepository charges, ChargeScheduler scheduler) {
        this.access = access;
        this.charges = charges;
        this.scheduler = scheduler;
    }

    @Override
    @Transactional(transactionManager = "bills")
    public void terminate(ChargeCommand command) {
        var charge = access.chargeForMember(command.householdId(), command.chargeId(), command.requesterId());
        charge.terminate();
        charges.save(charge);
        scheduler.reschedule(charge);
    }
}
