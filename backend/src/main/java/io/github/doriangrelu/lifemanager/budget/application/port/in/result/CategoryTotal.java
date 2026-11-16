package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import java.util.UUID;
/** Total des dépenses d'une catégorie pour le mois, en centimes. */
public record CategoryTotal(UUID categoryId, String name, long totalCents) {
}
