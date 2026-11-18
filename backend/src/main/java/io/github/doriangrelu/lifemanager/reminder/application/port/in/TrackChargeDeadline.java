package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.TrackChargeDeadlineCommand;

/** Suit la prochaine échéance ouverte d'une charge récurrente. */
public interface TrackChargeDeadline {

    void track(TrackChargeDeadlineCommand command);
}
