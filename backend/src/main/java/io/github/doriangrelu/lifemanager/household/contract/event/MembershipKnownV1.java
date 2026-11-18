package io.github.doriangrelu.lifemanager.household.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement public : rappel qu'un utilisateur est membre d'un foyer.
 * Publié pour chaque membre au démarrage : un domaine apparu après la création d'un foyer le découvre ainsi.
 * Les consommateurs doivent l'accepter plusieurs fois sans effet de bord.
 */
@Serdeable
public record MembershipKnownV1(UUID householdId, UUID memberId, Instant occurredAt) {
}
