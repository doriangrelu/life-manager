package io.github.doriangrelu.lifemanager.shopping.application.port.in.result;

import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.PurchaseHistoryEntry;

import java.time.Instant;
import java.util.UUID;

/** Un article de l'historique des achats tel que vu hors du domaine. */
public record PurchaseHistoryEntryView(UUID id, String label, Instant lastPurchasedAt, int purchaseCount) {

    public static PurchaseHistoryEntryView from(PurchaseHistoryEntry entry) {
        return new PurchaseHistoryEntryView(entry.id().value(), entry.label().value(), entry.lastPurchasedAt(),
                entry.purchaseCount());
    }
}
