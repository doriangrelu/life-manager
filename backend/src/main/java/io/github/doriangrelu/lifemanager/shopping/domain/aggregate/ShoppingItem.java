package io.github.doriangrelu.lifemanager.shopping.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemLabel;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemOrigin;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * Article d'une sous-liste de courses. Invariants : un libellé valide, une seule sous-liste.
 * Cocher ou décocher deux fois de suite est sans effet : deux membres peuvent faire le même geste en même temps.
 */
public final class ShoppingItem extends AggregateRoot<ShoppingItemId> {

    private final ShoppingListId listId;
    private final ItemLabel label;
    private final ItemOrigin origin;
    private Instant checkedAt;

    private ShoppingItem(ShoppingItemId id, ShoppingListId listId, ItemLabel label, ItemOrigin origin,
                         Instant checkedAt) {
        super(id);
        this.listId = Require.notNull(listId, "La sous-liste de l'article");
        this.label = Require.notNull(label, "Le libellé de l'article");
        this.origin = Require.notNull(origin, "L'origine de l'article");
        this.checkedAt = checkedAt;
    }

    /** Nouvel article, non coché. */
    public static ShoppingItem add(ShoppingItemId id, ShoppingListId listId, ItemLabel label, MemberId addedBy,
                                   Clock clock) {
        return new ShoppingItem(id, listId, label, new ItemOrigin(addedBy, clock.instant()), null);
    }

    /** Recharge un article depuis le stockage ; {@code checkedAt} vaut {@code null} s'il n'est pas coché. */
    public static ShoppingItem restore(ShoppingItemId id, ShoppingListId listId, ItemLabel label, ItemOrigin origin,
                                       Instant checkedAt) {
        return new ShoppingItem(id, listId, label, origin, checkedAt);
    }

    public void check(Clock clock) {
        if (checkedAt == null) {
            checkedAt = clock.instant();
        }
    }

    public void uncheck() {
        checkedAt = null;
    }

    public boolean isChecked() {
        return checkedAt != null;
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

    public ItemOrigin origin() {
        return origin;
    }

    public Optional<Instant> checkedAt() {
        return Optional.ofNullable(checkedAt);
    }
}
