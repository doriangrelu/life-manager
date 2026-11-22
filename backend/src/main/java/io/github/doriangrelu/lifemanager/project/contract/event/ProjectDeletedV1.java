package io.github.doriangrelu.lifemanager.project.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/** Événement public : un projet a été supprimé ; ses dépenses n'ont plus de projet. */
@Serdeable
public record ProjectDeletedV1(UUID projectId, UUID householdId, Instant occurredAt) {
}
