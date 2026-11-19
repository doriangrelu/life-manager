package io.github.doriangrelu.lifemanager.household.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/** Événement public : le foyer a été supprimé. Chaque domaine efface toutes ses données (et fichiers) de ce foyer. */
@Serdeable
public record HouseholdDeletedV1(UUID householdId, Instant occurredAt) {
}
