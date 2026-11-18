package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;

/**
 * Où en est une échéance. Une échéance payée a toujours un montant et une date de paiement, les autres jamais.
 *
 * @param status statut de l'échéance
 * @param paid   montant payé, {@code null} si elle n'est pas payée
 * @param paidOn jour du paiement, {@code null} si elle n'est pas payée
 */
public record Settlement(OccurrenceStatus status, Amount paid, LocalDate paidOn) {

    public static final Settlement UPCOMING = new Settlement(OccurrenceStatus.UPCOMING, null, null);
    public static final Settlement TO_CONFIRM = new Settlement(OccurrenceStatus.TO_CONFIRM, null, null);
    public static final Settlement SKIPPED = new Settlement(OccurrenceStatus.SKIPPED, null, null);

    public Settlement {
        Require.notNull(status, "Le statut de l'échéance");
        var isPaid = status == OccurrenceStatus.PAID;
        if (isPaid != (paid != null) || isPaid != (paidOn != null)) {
            throw new InvalidValueException("Une échéance payée a un montant et une date de paiement, les autres non");
        }
    }

    public static Settlement paid(Amount amount, LocalDate on) {
        return new Settlement(OccurrenceStatus.PAID, amount, on);
    }
}
