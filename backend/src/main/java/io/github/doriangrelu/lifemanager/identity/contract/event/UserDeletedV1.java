package io.github.doriangrelu.lifemanager.identity.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement public : un utilisateur a supprimé son compte.
 * Chaque foyer dont il est seul membre est supprimé ; il quitte les autres (transmis s'il en était propriétaire).
 */
@Serdeable
public record UserDeletedV1(UUID userId, Instant occurredAt) {
}
