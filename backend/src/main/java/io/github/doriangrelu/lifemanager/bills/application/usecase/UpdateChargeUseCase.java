package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.UpdateCharge;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.UpdateChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ChargeView;
import io.github.doriangrelu.lifemanager.bills.application.port.out.ChargeRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeInputs;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeScheduler;
import io.github.doriangrelu.lifemanager.bills.application.service.OpenOccurrences;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Modifie une charge ; ses échéances à venir sont recalculées. */
@Singleton
class UpdateChargeUseCase implements UpdateCharge {

    private final ChargeAccess access;
    private final ChargeInputs inputs;
    private final ChargeRepository charges;
    private final ChargeScheduler scheduler;
    private final OpenOccurrences open;

    UpdateChargeUseCase(ChargeAccess access, ChargeInputs inputs, ChargeRepository charges,
                        ChargeScheduler scheduler, OpenOccurrences open) {
        this.access = access;
        this.inputs = inputs;
        this.charges = charges;
        this.scheduler = scheduler;
        this.open = open;
    }

    @Override
    @Transactional(transactionManager = "bills")
    public ChargeView update(UpdateChargeCommand command) {
        var charge = access.chargeForMember(command.householdId(), command.chargeId(), command.requesterId());
        charge.update(inputs.details(charge.householdId(), command.input()), ChargeInputs.schedule(command.input()),
                open.clock());
        charges.save(charge);
        scheduler.reschedule(charge);
        return ChargeView.from(charge, open.nextOf(charge));
    }
}
