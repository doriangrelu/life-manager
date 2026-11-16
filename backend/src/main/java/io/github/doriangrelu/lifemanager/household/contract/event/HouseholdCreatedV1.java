package io.github.doriangrelu.lifemanager.household.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement public : un foyer a été créé, son fondateur en est le premier membre.
 * Contrat stable entre domaines (types simples, sérialisable en JSON pour un futur broker).
 */
@Serdeable
public record HouseholdCreatedV1(UUID householdId, UUID founderId, Instant occurredAt) {
}
