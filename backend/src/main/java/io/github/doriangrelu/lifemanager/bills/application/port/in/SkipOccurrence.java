package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.OccurrenceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.OccurrenceView;

/** Saute une échéance. */
public interface SkipOccurrence {

    OccurrenceView skip(OccurrenceCommand command);
}
