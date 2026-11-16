package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import java.util.UUID;
/** Total payé par un membre pour le mois, en centimes. */
public record PayerTotal(UUID memberId, long totalCents) {
}
