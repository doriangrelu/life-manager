package io.github.doriangrelu.lifemanager.bills.application.port.in.command;

/** Confirmation d'une échéance avec son montant réel. */
public record ConfirmOccurrenceCommand(OccurrenceCommand target, long amountCents) {
}
