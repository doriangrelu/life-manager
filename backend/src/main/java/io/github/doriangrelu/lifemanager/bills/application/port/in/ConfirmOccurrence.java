package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ConfirmOccurrenceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.OccurrenceView;

/** Paie une échéance à son montant réel. */
public interface ConfirmOccurrence {

    OccurrenceView confirm(ConfirmOccurrenceCommand command);
}
