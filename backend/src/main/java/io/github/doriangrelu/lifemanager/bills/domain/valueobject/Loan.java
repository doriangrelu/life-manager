package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/**
 * Ce qui fait d'une charge un crédit : la mensualité de son assurance, comprise dans le montant de la charge
 * (0 = pas d'assurance). La part du prêt est le montant de la charge moins l'assurance.
 */
public record Loan(Amount insurance) {

    public Loan {
        Require.notNull(insurance, "L'assurance du crédit");
    }

    /** Une assurance absente veut dire « ce n'est pas un crédit » : elle donne {@code null}. */
    public static Loan ofNullable(Long insuranceCents) {
        return insuranceCents == null ? null : new Loan(Amount.ofCents(insuranceCents));
    }
}
