package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.bills.contract.event.ChargeDeadlinesChangedV1;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.TrackChargeDeadline;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.TrackChargeDeadlineCommand;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/** Écoute les événements publics des charges récurrentes : les rappels suivent leurs échéances. */
@Singleton
class ChargeEventsListener {

    private final TrackChargeDeadline trackDeadline;

    ChargeEventsListener(TrackChargeDeadline trackDeadline) {
        this.trackDeadline = trackDeadline;
    }

    @EventListener
    void on(ChargeDeadlinesChangedV1 event) {
        trackDeadline.track(new TrackChargeDeadlineCommand(event.chargeId(), event.householdId(), event.chargeName(),
                event.nextDueOn(), event.awaitingConfirmation()));
    }
}
