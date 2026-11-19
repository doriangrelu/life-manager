package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.IncomeInput;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Corps de saisie ou de correction d'une rentrée.
 *
 * @param amountCents     montant en centimes d'euro (1 850,00 € = 185000)
 * @param categoryId      catégorie de rentrée existante — ou bien {@code newCategoryName}, jamais les deux
 * @param newCategoryName nom de catégorie tapé librement, créée si le foyer ne l'a pas
 * @param receivedBy      membre qui la reçoit, ou {@code null} pour le foyer entier
 */
@Serdeable
public record IncomeRequest(@Positive @Max(100_000_000) long amountCents,
                            @Nullable UUID categoryId,
                            @Nullable @Size(max = 40) String newCategoryName,
                            @NotNull LocalDate receivedOn,
                            @Nullable UUID receivedBy,
                            @Nullable @Size(max = 140) String label) {

    public IncomeInput toInput() {
        return new IncomeInput(amountCents, categoryId, newCategoryName, receivedOn, receivedBy, label);
    }
}
