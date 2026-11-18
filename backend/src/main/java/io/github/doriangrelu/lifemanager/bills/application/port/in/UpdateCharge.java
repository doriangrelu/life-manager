package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.UpdateChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ChargeView;

/** Modifie une charge (vaut pour les échéances à venir). */
public interface UpdateCharge {

    ChargeView update(UpdateChargeCommand command);
}
