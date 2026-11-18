package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ChargeCommand;

/** Résilie une charge. */
public interface TerminateCharge {

    void terminate(ChargeCommand command);
}
