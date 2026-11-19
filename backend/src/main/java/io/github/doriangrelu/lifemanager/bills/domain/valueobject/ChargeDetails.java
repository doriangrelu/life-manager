package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/**
 * Ce qu'un membre saisit sur une charge, déjà vérifié dans sa forme.
 *
 * @param name             nom de la charge
 * @param amount           montant prévu de chaque échéance
 * @param kind             montant fixe (payé seul) ou variable (à confirmer)
 * @param budgetCategoryId catégorie du budget (identifiant du domaine budget, vérifié par lui), du même sens
 * @param payer            membre qui paie ; pour une rentrée, membre qui la reçoit
 * @param beneficiary      membre pour qui, ou {@code null} pour tout le foyer (sans objet pour une rentrée)
 * @param direction        dépense ou rentrée
 */
public record ChargeDetails(ChargeName name, Amount amount, AmountKind kind, UUID budgetCategoryId, MemberId payer,
                            MemberId beneficiary, FlowDirection direction) {

    public ChargeDetails {
        Require.notNull(name, "Le nom de la charge");
        Require.notNull(amount, "Le montant");
        Require.notNull(kind, "Le type de montant");
        Require.notNull(budgetCategoryId, "La catégorie");
        Require.notNull(payer, "Le payeur");
        Require.notNull(direction, "Le sens");
        if (direction == FlowDirection.INCOME) {
            beneficiary = null; // une rentrée n'a pas de bénéficiaire : seul compte qui la reçoit
        }
    }
}
