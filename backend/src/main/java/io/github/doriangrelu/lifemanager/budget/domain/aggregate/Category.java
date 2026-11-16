package io.github.doriangrelu.lifemanager.budget.domain.aggregate;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.List;

/**
 * Catégorie de dépenses d'un foyer. Une catégorie archivée n'est plus proposée pour les nouvelles dépenses,
 * mais les dépenses déjà classées la gardent. Invariants : un nom valide, un seul foyer.
 */
public final class Category extends AggregateRoot<CategoryId> {

    /** Catégories reçues par tout nouveau foyer. */
    public static final List<String> DEFAULT_NAMES = List.of(
            "Courses", "Logement", "Transport", "Santé", "Loisirs", "Restaurants", "Abonnements", "Autre");

    private final HouseholdId householdId;
    private CategoryName name;
    private boolean archived;

    private Category(CategoryId id, HouseholdId householdId, CategoryName name, boolean archived) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer de la catégorie");
        this.name = Require.notNull(name, "Le nom de la catégorie");
        this.archived = archived;
    }

    /** Nouvelle catégorie, active. */
    public static Category create(CategoryId id, HouseholdId householdId, CategoryName name) {
        return new Category(id, householdId, name, false);
    }

    /** Les catégories par défaut d'un nouveau foyer. */
    public static List<Category> defaultsFor(HouseholdId householdId) {
        return DEFAULT_NAMES.stream()
                .map(name -> create(CategoryId.newId(), householdId, new CategoryName(name)))
                .toList();
    }

    /** Recharge une catégorie depuis le stockage. */
    public static Category restore(CategoryId id, HouseholdId householdId, CategoryName name, boolean archived) {
        return new Category(id, householdId, name, archived);
    }

    public void rename(CategoryName newName) {
        name = Require.notNull(newName, "Le nom de la catégorie");
    }

    public void archive() {
        archived = true;
    }

    public void reactivate() {
        archived = false;
    }

    /** Vrai si la catégorie peut recevoir de nouvelles dépenses de ce foyer. */
    public boolean isUsableBy(HouseholdId household) {
        return !archived && householdId.equals(household);
    }

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public CategoryName name() {
        return name;
    }

    public boolean archived() {
        return archived;
    }
}
