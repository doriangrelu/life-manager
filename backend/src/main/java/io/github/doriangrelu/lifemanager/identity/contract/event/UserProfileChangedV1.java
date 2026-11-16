package io.github.doriangrelu.lifemanager.identity.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement public : l'email ou le nom d'affichage d'un utilisateur a changé.
 * Contrat stable entre domaines (types simples, sérialisable en JSON pour un futur broker).
 */
@Serdeable
public record UserProfileChangedV1(UUID userId, String email, String displayName, Instant occurredAt) {
}
