package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/**
 * Ce qu'un membre saisit sur une charge, déjà vérifié dans sa forme.
 *
 * @param name             nom de la charge
 * @param amount           montant prévu de chaque échéance
 * @param kind             montant fixe (payé seul) ou variable (à confirmer)
 * @param budgetCategoryId catégorie du budget (identifiant du domaine budget, vérifié par lui)
 * @param payer            membre qui paie
 * @param beneficiary      membre pour qui, ou {@code null} pour tout le foyer
 */
public record ChargeDetails(ChargeName name, Amount amount, AmountKind kind, UUID budgetCategoryId, MemberId payer,
                            MemberId beneficiary) {

    public ChargeDetails {
        Require.notNull(name, "Le nom de la charge");
        Require.notNull(amount, "Le montant");
        Require.notNull(kind, "Le type de montant");
        Require.notNull(budgetCategoryId, "La catégorie");
        Require.notNull(payer, "Le payeur");
    }
}
