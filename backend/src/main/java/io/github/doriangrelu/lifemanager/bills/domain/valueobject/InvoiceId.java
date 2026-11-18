package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant de la facture. */
public record InvoiceId(UUID value) {

    public InvoiceId {
        Require.notNull(value, "L'identifiant de la facture");
    }

    public static InvoiceId newId() {
        return new InvoiceId(UUID.randomUUID());
    }
}
