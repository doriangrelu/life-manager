package io.github.doriangrelu.lifemanager.shopping.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ListName;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;

import java.util.List;

/**
 * Sous-liste de courses d'un foyer (« Courses », « Bricolage »…). Invariants : un nom valide, un seul foyer.
 * Elle ne contient pas ses articles : chaque article est un agrégat à part, que deux membres cochent en même temps.
 */
public final class ShoppingList extends AggregateRoot<ShoppingListId> {

    /** Sous-listes reçues par tout nouveau foyer. */
    public static final List<String> DEFAULT_NAMES = List.of("Courses", "Bricolage", "Vêtements");

    private final HouseholdId householdId;
    private ListName name;

    private ShoppingList(ShoppingListId id, HouseholdId householdId, ListName name) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer de la sous-liste");
        this.name = Require.notNull(name, "Le nom de la sous-liste");
    }

    public static ShoppingList create(ShoppingListId id, HouseholdId householdId, ListName name) {
        return new ShoppingList(id, householdId, name);
    }

    /** Les sous-listes par défaut d'un nouveau foyer. */
    public static List<ShoppingList> defaultsFor(HouseholdId householdId) {
        return DEFAULT_NAMES.stream()
                .map(name -> create(ShoppingListId.newId(), householdId, new ListName(name)))
                .toList();
    }

    /** Recharge une sous-liste depuis le stockage. */
    public static ShoppingList restore(ShoppingListId id, HouseholdId householdId, ListName name) {
        return new ShoppingList(id, householdId, name);
    }

    public void rename(ListName newName) {
        name = Require.notNull(newName, "Le nom de la sous-liste");
    }

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public ListName name() {
        return name;
    }
}
