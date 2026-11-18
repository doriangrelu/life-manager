package io.github.doriangrelu.lifemanager.shopping.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemLabel;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.PurchaseHistoryEntryId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurchaseHistoryEntryTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-16T10:00:00Z"), ZoneId.of("Europe/Paris"));

    @Test
    void a_first_purchase_counts_once() {
        var entry = milk();

        assertThat(entry.purchaseCount()).isEqualTo(1);
        assertThat(entry.lastPurchasedAt()).isEqualTo(CLOCK.instant());
    }

    @Test
    void buying_again_increments_and_moves_the_date() {
        var entry = milk();
        var nextWeek = Clock.offset(CLOCK, Duration.ofDays(7));

        entry.recordPurchase(nextWeek);

        assertThat(entry.purchaseCount()).isEqualTo(2);
        assertThat(entry.lastPurchasedAt()).isEqualTo(nextWeek.instant());
    }

    @Test
    void an_entry_was_bought_at_least_once() {
        assertThatThrownBy(() -> PurchaseHistoryEntry.restore(PurchaseHistoryEntryId.newId(), ShoppingListId.newId(),
                new ItemLabel("Lait"), CLOCK.instant(), 0)).isInstanceOf(InvalidValueException.class);
    }

    private static PurchaseHistoryEntry milk() {
        return PurchaseHistoryEntry.firstPurchase(PurchaseHistoryEntryId.newId(), ShoppingListId.newId(),
                new ItemLabel("Lait"), CLOCK);
    }
}
