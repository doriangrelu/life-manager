package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.util.UUID;

/**
 * État d'un projet annoncé par le domaine project.
 *
 * @param status ACTIVE, DONE ou ABANDONED
 */
public record TrackProjectCommand(UUID projectId, UUID householdId, String name, String status,
                                  boolean countsInBudget) {
}
