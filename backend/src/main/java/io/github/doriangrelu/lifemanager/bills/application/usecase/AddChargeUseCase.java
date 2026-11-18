package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.AddCharge;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.AddChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ChargeView;
import io.github.doriangrelu.lifemanager.bills.application.port.out.ChargeRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeInputs;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeScheduler;
import io.github.doriangrelu.lifemanager.bills.application.service.OpenOccurrences;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.RecurringCharge;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Enregistre une charge et prévoit aussitôt sa première échéance. */
@Singleton
class AddChargeUseCase implements AddCharge {

    private final ChargeAccess access;
    private final ChargeInputs inputs;
    private final ChargeRepository charges;
    private final ChargeScheduler scheduler;
    private final OpenOccurrences open;

    AddChargeUseCase(ChargeAccess access, ChargeInputs inputs, ChargeRepository charges, ChargeScheduler scheduler,
                     OpenOccurrences open) {
        this.access = access;
        this.inputs = inputs;
        this.charges = charges;
        this.scheduler = scheduler;
        this.open = open;
    }

    @Override
    @Transactional(transactionManager = "bills")
    public ChargeView add(AddChargeCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var charge = RecurringCharge.create(ChargeId.newId(), household, inputs.details(household, command.input()),
                ChargeInputs.schedule(command.input()), open.clock());
        charges.save(charge);
        scheduler.advance(charge);
        return ChargeView.from(charge, open.nextOf(charge));
    }
}
