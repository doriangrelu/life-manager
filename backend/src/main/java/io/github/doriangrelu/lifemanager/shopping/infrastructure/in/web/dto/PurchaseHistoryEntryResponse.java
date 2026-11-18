package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.PurchaseHistoryEntryView;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/** Un article de l'historique des achats d'une sous-liste. */
@Serdeable
public record PurchaseHistoryEntryResponse(UUID id, String label, Instant lastPurchasedAt, int purchaseCount) {

    public static PurchaseHistoryEntryResponse from(PurchaseHistoryEntryView entry) {
        return new PurchaseHistoryEntryResponse(entry.id(), entry.label(), entry.lastPurchasedAt(),
                entry.purchaseCount());
    }
}
