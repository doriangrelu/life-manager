package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;

import java.util.List;
import java.util.UUID;

/** Total d'une catégorie (dépenses ou rentrées) sur une période, en centimes. */
public record CategoryTotal(UUID categoryId, String name, long totalCents) {

    /** Somme exacte des totaux, en centimes (Money refuse tout dépassement de capacité). */
    public static long sum(List<CategoryTotal> totals) {
        return totals.stream()
                .map(total -> Money.ofCents(total.totalCents()))
                .reduce(Money.ZERO, Money::plus)
                .cents();
    }
}
