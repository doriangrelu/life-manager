package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.AddChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ChargeView;

/** Enregistre une charge récurrente du foyer. */
public interface AddCharge {

    ChargeView add(AddChargeCommand command);
}
