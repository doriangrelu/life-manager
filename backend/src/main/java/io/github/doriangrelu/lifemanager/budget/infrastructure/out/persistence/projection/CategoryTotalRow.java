package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.projection;

import io.micronaut.core.annotation.Introspected;

import java.util.UUID;

/** Résultat d'une somme groupée par catégorie (montant en centimes). */
@Introspected
public record CategoryTotalRow(UUID categoryId, String name, long totalCents) {
}
