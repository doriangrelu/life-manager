package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.util.UUID;
/** Foyer qui vient d'être créé. */
public record CreateDefaultCategoriesCommand(UUID householdId) {
}
