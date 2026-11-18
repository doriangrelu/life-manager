package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.AdvanceChargeSchedules;
import io.github.doriangrelu.lifemanager.bills.application.port.out.ChargeRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ScheduleRunner;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Fait avancer toutes les charges actives, chacune dans sa transaction. */
@Singleton
class AdvanceChargeSchedulesUseCase implements AdvanceChargeSchedules {

    private static final Logger LOG = LoggerFactory.getLogger(AdvanceChargeSchedulesUseCase.class);

    private final ChargeRepository charges;
    private final ScheduleRunner runner;

    AdvanceChargeSchedulesUseCase(ChargeRepository charges, ScheduleRunner runner) {
        this.charges = charges;
        this.runner = runner;
    }

    @Override
    public void advanceAll() {
        for (var chargeId : charges.findAllActiveIds()) {
            try {
                runner.advance(chargeId);
            } catch (RuntimeException e) {
                // Une charge en échec n'empêche pas les autres ; elle sera reprise au prochain passage.
                LOG.error("Calendrier de la charge {} non avancé", chargeId.value(), e);
            }
        }
    }
}
