package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import java.util.UUID;
/**
 * Total dépensé pour un bénéficiaire pour le mois, en centimes.
 *
 * @param memberId membre bénéficiaire, ou {@code null} pour les dépenses communes
 */
public record BeneficiaryTotal(UUID memberId, long totalCents) {
}
