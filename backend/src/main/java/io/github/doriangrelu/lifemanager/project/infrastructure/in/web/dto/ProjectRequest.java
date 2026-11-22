package io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.ProjectInput;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Corps d'ajout ou de modification d'un projet.
 *
 * @param color          INDIGO, TEAL, GREEN, AMBER, ORANGE, RED, PINK ou SLATE
 * @param status         ACTIVE, DONE ou ABANDONED (ACTIVE si absent)
 * @param countsInBudget vrai si absent
 */
@Serdeable
public record ProjectRequest(@NotBlank @Size(max = 60) String name,
                             @Nullable @Size(max = 500) String description,
                             @NotBlank String color,
                             @Nullable @PositiveOrZero Long plannedCents,
                             @Nullable LocalDate startOn,
                             @Nullable LocalDate targetOn,
                             @Nullable String status,
                             @Nullable Boolean countsInBudget) {

    public ProjectInput toInput() {
        return new ProjectInput(name, description, color, plannedCents, startOn, targetOn,
                status == null ? "ACTIVE" : status, countsInBudget == null || countsInBudget);
    }
}
