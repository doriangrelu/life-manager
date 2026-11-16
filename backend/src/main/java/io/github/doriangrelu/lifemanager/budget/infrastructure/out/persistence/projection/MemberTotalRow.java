package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.projection;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

import java.util.UUID;

/**
 * Résultat d'une somme groupée par membre (payeur ou bénéficiaire), montant en centimes.
 *
 * @param memberId membre, ou {@code null} pour les dépenses communes (bénéficiaire)
 */
@Introspected
public record MemberTotalRow(@Nullable UUID memberId, long totalCents) {
}
