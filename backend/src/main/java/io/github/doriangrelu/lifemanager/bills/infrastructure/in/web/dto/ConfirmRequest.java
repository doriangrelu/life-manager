package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;

/** Montant réel d'une échéance, en centimes. */
@Serdeable
public record ConfirmRequest(long amountCents) {
}
