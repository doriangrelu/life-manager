package io.github.doriangrelu.lifemanager.shopping.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une ligne de l'historique des achats. */
public record PurchaseHistoryEntryId(UUID value) {

    public PurchaseHistoryEntryId {
        Require.notNull(value, "L'identifiant de l'historique");
    }

    public static PurchaseHistoryEntryId newId() {
        return new PurchaseHistoryEntryId(UUID.randomUUID());
    }
}
