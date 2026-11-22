package io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/** Nouvel ordre des tâches d'un projet. */
@Serdeable
public record TaskOrderRequest(@NotNull List<UUID> ids) {
}
