package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Champs d'une rentrée tels que saisis (types simples, pas encore vérifiés).
 * La catégorie est donnée soit par {@code categoryId} (existante), soit par {@code newCategoryName}.
 *
 * @param amountCents     montant en centimes d'euro
 * @param categoryId      catégorie de rentrée existante, ou {@code null}
 * @param newCategoryName nom de catégorie tapé librement (créée si besoin), ou {@code null}
 * @param receivedOn      jour où l'argent est arrivé
 * @param receivedBy      membre qui la reçoit, ou {@code null} pour le foyer entier
 * @param label           libellé, ou {@code null}
 */
public record IncomeInput(long amountCents, UUID categoryId, String newCategoryName, LocalDate receivedOn,
                          UUID receivedBy, String label) {
}
