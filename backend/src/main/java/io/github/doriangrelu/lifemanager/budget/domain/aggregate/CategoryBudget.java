package io.github.doriangrelu.lifemanager.budget.domain.aggregate;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.BudgetScope;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryBudgetId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.PlannedAmount;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.YearMonth;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Règle de budget prévu d'une catégorie : un montant (ou « pas de prévu »), à partir d'un mois ou pour ce mois seulement.
 * Les règles sont datées : en changer une ne modifie jamais le prévu des mois précédents.
 * Pour un mois donné, la règle « ce mois seulement » l'emporte, sinon la règle « à partir de » la plus récente.
 */
public final class CategoryBudget extends AggregateRoot<CategoryBudgetId> {

    private final HouseholdId householdId;
    private final CategoryId categoryId;
    private final YearMonth month;
    private final BudgetScope scope;
    /** {@code null} : la règle retire le prévu. */
    private final PlannedAmount planned;

    private CategoryBudget(CategoryBudgetId id, HouseholdId householdId, CategoryId categoryId, Period period,
                           PlannedAmount planned) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer du budget prévu");
        this.categoryId = Require.notNull(categoryId, "La catégorie du budget prévu");
        this.month = period.month();
        this.scope = period.scope();
        this.planned = planned;
    }

    /** Mois et portée d'une règle. */
    public record Period(YearMonth month, BudgetScope scope) {

        public Period {
            Require.notNull(month, "Le mois du budget prévu");
            Require.notNull(scope, "La portée du budget prévu");
        }
    }

    /** Fixe (ou retire, si {@code planned} est absent) le prévu d'une catégorie pour une période. */
    public static CategoryBudget set(HouseholdId householdId, CategoryId categoryId, Period period,
                                     PlannedAmount planned) {
        return new CategoryBudget(CategoryBudgetId.newId(), householdId, categoryId, period, planned);
    }

    /** Recharge une règle depuis le stockage. */
    public static CategoryBudget restore(CategoryBudgetId id, HouseholdId householdId, CategoryId categoryId,
                                         Period period, PlannedAmount planned) {
        return new CategoryBudget(id, householdId, categoryId, period, planned);
    }

    /** Les règles qui s'appliquent ce mois-là et fixent un montant : une au plus par catégorie. */
    public static List<CategoryBudget> applicableIn(Collection<CategoryBudget> rules, YearMonth wanted) {
        var winners = new LinkedHashMap<CategoryId, CategoryBudget>();
        rules.stream()
                .filter(rule -> rule.appliesTo(wanted))
                .forEach(rule -> winners.merge(rule.categoryId, rule,
                        (current, candidate) -> candidate.overrides(current) ? candidate : current));
        return winners.values().stream().filter(rule -> Objects.nonNull(rule.planned)).toList();
    }

    public boolean appliesTo(YearMonth wanted) {
        return scope == BudgetScope.MONTH_ONLY ? month.equals(wanted) : !month.isAfter(wanted);
    }

    /** Entre deux règles applicables au même mois : « ce mois seulement » d'abord, sinon la plus récente. */
    private boolean overrides(CategoryBudget other) {
        if (scope != other.scope) {
            return scope == BudgetScope.MONTH_ONLY;
        }
        return month.isAfter(other.month);
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public CategoryId categoryId() {
        return categoryId;
    }

    public YearMonth month() {
        return month;
    }

    public BudgetScope scope() {
        return scope;
    }

    public Optional<PlannedAmount> planned() {
        return Optional.ofNullable(planned);
    }
}
