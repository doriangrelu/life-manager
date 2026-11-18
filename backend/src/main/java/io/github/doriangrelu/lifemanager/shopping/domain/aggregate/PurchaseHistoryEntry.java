package io.github.doriangrelu.lifemanager.shopping.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemLabel;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.PurchaseHistoryEntryId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;

import java.time.Clock;
import java.time.Instant;

/**
 * Article déjà acheté dans une sous-liste : une seule ligne par libellé, avec le dernier achat et le nombre d'achats.
 * Sert à remonter rapidement une liste à partir des achats habituels.
 */
public final class PurchaseHistoryEntry extends AggregateRoot<PurchaseHistoryEntryId> {

    private final ShoppingListId listId;
    private final ItemLabel label;
    private Instant lastPurchasedAt;
    private int purchaseCount;

    private PurchaseHistoryEntry(PurchaseHistoryEntryId id, ShoppingListId listId, ItemLabel label,
                                 Instant lastPurchasedAt, int purchaseCount) {
        super(id);
        this.listId = Require.notNull(listId, "La sous-liste de l'historique");
        this.label = Require.notNull(label, "Le libellé de l'article");
        this.lastPurchasedAt = Require.notNull(lastPurchasedAt, "La date du dernier achat");
        if (purchaseCount < 1) {
            throw new InvalidValueException("Un article de l'historique a été acheté au moins une fois");
        }
        this.purchaseCount = purchaseCount;
    }

    /** Premier achat d'un libellé dans cette sous-liste. */
    public static PurchaseHistoryEntry firstPurchase(PurchaseHistoryEntryId id, ShoppingListId listId, ItemLabel label,
                                                     Clock clock) {
        return new PurchaseHistoryEntry(id, listId, label, clock.instant(), 1);
    }

    /** Recharge une ligne d'historique depuis le stockage. */
    public static PurchaseHistoryEntry restore(PurchaseHistoryEntryId id, ShoppingListId listId, ItemLabel label,
                                               Instant lastPurchasedAt, int purchaseCount) {
        return new PurchaseHistoryEntry(id, listId, label, lastPurchasedAt, purchaseCount);
    }

    /** Le même article vient d'être acheté de nouveau. */
    public void recordPurchase(Clock clock) {
        purchaseCount++;
        lastPurchasedAt = clock.instant();
    }

    public boolean belongsTo(ShoppingListId list) {
        return listId.equals(list);
    }

    public ShoppingListId listId() {
        return listId;
    }

    public ItemLabel label() {
        return label;
    }

    public Instant lastPurchasedAt() {
        return lastPurchasedAt;
    }

    public int purchaseCount() {
        return purchaseCount;
    }
}
