package io.github.doriangrelu.lifemanager.shopping.application.port.in.command;

import java.util.UUID;

/** Un foyer vient d'être créé : il reçoit ses sous-listes par défaut. */
public record CreateDefaultShoppingListsCommand(UUID householdId) {
}
