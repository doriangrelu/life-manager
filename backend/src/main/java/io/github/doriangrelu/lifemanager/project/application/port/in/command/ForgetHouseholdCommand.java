package io.github.doriangrelu.lifemanager.project.application.port.in.command;

import java.util.UUID;

/** Le foyer a été supprimé : toutes ses données disparaissent. */
public record ForgetHouseholdCommand(UUID householdId) {
}
