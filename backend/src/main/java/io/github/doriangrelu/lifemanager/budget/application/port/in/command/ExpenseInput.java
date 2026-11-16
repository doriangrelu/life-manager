package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.time.LocalDate;
import java.util.UUID;
/**
 * Champs d'une dépense tels que saisis (types simples, pas encore vérifiés).
 *
 * @param amountCents montant en centimes d'euro
 * @param categoryId  catégorie choisie
 * @param spentOn     jour de la dépense
 * @param paidBy      membre qui a payé
 * @param beneficiary membre pour qui, ou {@code null} pour une dépense commune
 * @param label       libellé, ou {@code null}
 */
public record ExpenseInput(long amountCents, UUID categoryId, LocalDate spentOn, UUID paidBy, UUID beneficiary,
                           String label) {
}
