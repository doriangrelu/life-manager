package io.github.doriangrelu.lifemanager.reminder.application.port.in.command;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Prochaine échéance ouverte d'une charge.
 *
 * @param nextDueOn            {@code null} s'il n'y en a plus : la charge n'a plus de rappel
 * @param awaitingConfirmation vrai si cette échéance attend la confirmation de son montant
 */
public record TrackChargeDeadlineCommand(UUID chargeId, UUID householdId, String chargeName, LocalDate nextDueOn,
                                         boolean awaitingConfirmation) {
}
