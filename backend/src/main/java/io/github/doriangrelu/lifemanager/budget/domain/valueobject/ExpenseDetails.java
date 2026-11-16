package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Ce que l'utilisateur saisit ou corrige sur une dépense, déjà vérifié dans sa forme.
 *
 * @param amount      montant, strictement positif et au plus {@link #MAX_AMOUNT}
 * @param categoryId  catégorie de la dépense
 * @param spentOn     jour de la dépense
 * @param paidBy      membre qui a payé
 * @param beneficiary pour qui (commun ou un membre)
 * @param label       libellé, {@code null} s'il n'y en a pas (voir {@link #optionalLabel()})
 */
public record ExpenseDetails(Money amount, CategoryId categoryId, LocalDate spentOn, MemberId paidBy,
                             Beneficiary beneficiary, ExpenseLabel label) {

    /** Plafond d'une dépense : 1 000 000 €. */
    public static final Money MAX_AMOUNT = Money.ofCents(100_000_000L);

    public ExpenseDetails {
        Require.notNull(amount, "Le montant");
        if (amount.isZero() || amount.compareTo(MAX_AMOUNT) > 0) {
            throw new InvalidValueException("Le montant doit être compris entre 0,01 € et 1 000 000 €");
        }
        Require.notNull(categoryId, "La catégorie");
        Require.notNull(spentOn, "La date");
        Require.notNull(paidBy, "Le payeur");
        Require.notNull(beneficiary, "Le bénéficiaire");
    }

    public Optional<ExpenseLabel> optionalLabel() {
        return Optional.ofNullable(label);
    }
}
