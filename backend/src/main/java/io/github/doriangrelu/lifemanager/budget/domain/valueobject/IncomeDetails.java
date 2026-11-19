package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Ce que l'utilisateur saisit ou corrige sur une rentrée d'argent, déjà vérifié dans sa forme.
 *
 * @param amount     montant, strictement positif et au plus {@link ExpenseDetails#MAX_AMOUNT}
 * @param categoryId catégorie de rentrée
 * @param receivedOn jour où l'argent est arrivé
 * @param receivedBy qui la reçoit : un membre, ou le foyer entier ({@link Beneficiary#SHARED})
 * @param label      libellé, {@code null} s'il n'y en a pas
 */
public record IncomeDetails(Money amount, CategoryId categoryId, LocalDate receivedOn, Beneficiary receivedBy,
                            ExpenseLabel label) {

    public IncomeDetails {
        Require.notNull(amount, "Le montant");
        if (amount.isZero() || amount.compareTo(ExpenseDetails.MAX_AMOUNT) > 0) {
            throw new InvalidValueException("Le montant doit être compris entre 0,01 € et 1 000 000 €");
        }
        Require.notNull(categoryId, "La catégorie");
        Require.notNull(receivedOn, "La date");
        Require.notNull(receivedBy, "Le bénéficiaire");
    }

    public Optional<ExpenseLabel> optionalLabel() {
        return Optional.ofNullable(label);
    }
}
