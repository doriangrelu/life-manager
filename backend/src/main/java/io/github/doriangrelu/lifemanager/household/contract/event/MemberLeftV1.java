package io.github.doriangrelu.lifemanager.household.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement public : un membre a quitté un foyer ou en a été retiré. Chaque domaine lui coupe l'accès.
 *
 * @param ownerId propriétaire du foyer à ce moment-là (ex. il reprend les charges que payait le membre parti)
 */
@Serdeable
public record MemberLeftV1(UUID householdId, UUID memberId, UUID ownerId, Instant occurredAt) {
}
