package io.github.doriangrelu.lifemanager.household.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement public : un utilisateur a rejoint un foyer existant.
 * Contrat stable entre domaines (types simples, sérialisable en JSON pour un futur broker).
 */
@Serdeable
public record MemberJoinedV1(UUID householdId, UUID memberId, Instant occurredAt) {
}
