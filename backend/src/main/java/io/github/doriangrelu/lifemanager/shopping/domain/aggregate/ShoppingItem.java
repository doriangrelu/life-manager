package io.github.doriangrelu.lifemanager.shopping.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemLabel;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemOrigin;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * Article de la liste de courses d'un foyer. Invariants : un libellé valide, un seul foyer.
 * Cocher ou décocher deux fois de suite est sans effet : deux membres peuvent faire le même geste en même temps.
 */
public final class ShoppingItem extends AggregateRoot<ShoppingItemId> {

    private final HouseholdId householdId;
    private final ItemLabel label;
    private final ItemOrigin origin;
    private Instant checkedAt;

    private ShoppingItem(ShoppingItemId id, HouseholdId householdId, ItemLabel label, ItemOrigin origin,
                         Instant checkedAt) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer de l'article");
        this.label = Require.notNull(label, "Le libellé de l'article");
        this.origin = Require.notNull(origin, "L'origine de l'article");
        this.checkedAt = checkedAt;
    }

    /** Nouvel article, non coché. */
    public static ShoppingItem add(ShoppingItemId id, HouseholdId householdId, ItemLabel label, MemberId addedBy,
                                   Clock clock) {
        return new ShoppingItem(id, householdId, label, new ItemOrigin(addedBy, clock.instant()), null);
    }

    /** Recharge un article depuis le stockage ; {@code checkedAt} vaut {@code null} s'il n'est pas coché. */
    public static ShoppingItem restore(ShoppingItemId id, HouseholdId householdId, ItemLabel label, ItemOrigin origin,
                                       Instant checkedAt) {
        return new ShoppingItem(id, householdId, label, origin, checkedAt);
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

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    public HouseholdId householdId() {
        return householdId;
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
