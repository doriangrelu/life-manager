package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

/** Étape d'une échéance : à venir, à confirmer (montant variable), payée ou sautée. */
public enum OccurrenceStatus {
    UPCOMING,
    TO_CONFIRM,
    PAID,
    SKIPPED;

    /** Une échéance encore ouverte peut être confirmée ou sautée. */
    public boolean isOpen() {
        return this == UPCOMING || this == TO_CONFIRM;
    }
}
