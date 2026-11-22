package io.github.doriangrelu.lifemanager.project.application.port.in.command;

import java.time.LocalDate;

/**
 * Saisie d'un projet, partagée par l'ajout et la modification.
 *
 * @param color  INDIGO, TEAL, GREEN, AMBER, ORANGE, RED, PINK ou SLATE
 * @param status ACTIVE, DONE ou ABANDONED
 */
public record ProjectInput(String name, String description, String color, Long plannedCents, LocalDate startOn,
                           LocalDate targetOn, String status, boolean countsInBudget) {
}
