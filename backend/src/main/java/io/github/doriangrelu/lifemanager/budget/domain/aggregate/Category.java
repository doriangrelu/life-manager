package io.github.doriangrelu.lifemanager.budget.domain.aggregate;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.List;

/**
 * Catégorie d'un foyer, pour ses dépenses ou pour ses rentrées (son sens ne change jamais).
 * Une catégorie archivée n'est plus proposée, mais ce qui y est déjà classé la garde.
 * Invariants : un nom valide, un seul foyer, un seul sens.
 */
public final class Category extends AggregateRoot<CategoryId> {

    /** Catégories de dépenses reçues par tout nouveau foyer. */
    public static final List<String> DEFAULT_NAMES = List.of(
            "Courses", "Logement", "Transport", "Santé", "Loisirs", "Restaurants", "Abonnements", "Autre");

    /** Catégories de rentrées proposées au premier usage du suivi des rentrées. */
    public static final List<String> DEFAULT_INCOME_NAMES = List.of(
            "Salaire", "Prime", "Remboursement", "Aides", "Autre");

    private final HouseholdId householdId;
    private final CategoryKind kind;
    private CategoryName name;
    private boolean archived;

    private Category(CategoryId id, HouseholdId householdId, CategoryKind kind, CategoryName name, boolean archived) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer de la catégorie");
        this.kind = Require.notNull(kind, "Le sens de la catégorie");
        this.name = Require.notNull(name, "Le nom de la catégorie");
        this.archived = archived;
    }

    /** Nouvelle catégorie de dépenses, active. */
    public static Category create(CategoryId id, HouseholdId householdId, CategoryName name) {
        return create(id, householdId, name, CategoryKind.EXPENSE);
    }

    /** Nouvelle catégorie active, du sens donné. */
    public static Category create(CategoryId id, HouseholdId householdId, CategoryName name, CategoryKind kind) {
        return new Category(id, householdId, kind, name, false);
    }

    /** Les catégories de dépenses par défaut d'un nouveau foyer. */
    public static List<Category> defaultsFor(HouseholdId householdId) {
        return defaultsFor(householdId, CategoryKind.EXPENSE);
    }

    /** Les catégories par défaut d'un foyer, pour un sens. */
    public static List<Category> defaultsFor(HouseholdId householdId, CategoryKind kind) {
        var names = kind == CategoryKind.INCOME ? DEFAULT_INCOME_NAMES : DEFAULT_NAMES;
        return names.stream()
                .map(name -> create(CategoryId.newId(), householdId, new CategoryName(name), kind))
                .toList();
    }

    /** Recharge une catégorie depuis le stockage. */
    public static Category restore(CategoryId id, HouseholdId householdId, CategoryName name, boolean archived,
                                   CategoryKind kind) {
        return new Category(id, householdId, kind, name, archived);
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

    /** Vrai si la catégorie, active, appartient à ce foyer (quel que soit son sens). */
    public boolean isUsableBy(HouseholdId household) {
        return !archived && householdId.equals(household);
    }

    /** Vrai si la catégorie peut classer un nouvel élément de ce sens (dépense ou rentrée) pour ce foyer. */
    public boolean isUsableFor(HouseholdId household, CategoryKind wanted) {
        return isUsableBy(household) && kind == wanted;
    }

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public CategoryKind kind() {
        return kind;
    }

    public CategoryName name() {
        return name;
    }

    public boolean archived() {
        return archived;
    }
}
