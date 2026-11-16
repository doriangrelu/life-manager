package io.github.doriangrelu.lifemanager.identity.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement public : un nouvel utilisateur a été enregistré.
 * Contrat stable entre domaines (types simples, sérialisable en JSON pour un futur broker).
 */
@Serdeable
public record UserRegisteredV1(UUID userId, String email, String displayName, Instant occurredAt) {
}
