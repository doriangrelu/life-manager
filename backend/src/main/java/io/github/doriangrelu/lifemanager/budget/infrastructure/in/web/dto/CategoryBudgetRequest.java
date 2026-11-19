package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

/**
 * Corps du réglage d'un budget prévu.
 *
 * @param month        mois de départ, ou seul mois concerné (« 2026-11 »)
 * @param scope        FROM_MONTH (ce mois et les suivants) ou MONTH_ONLY (ce mois seulement)
 * @param plannedCents montant prévu en centimes d'euro (400 € = 40000) ; absent = retirer le prévu
 */
@Serdeable
public record CategoryBudgetRequest(@NotBlank String month,
                                    @NotBlank String scope,
                                    @Nullable @Positive @Max(100_000_000) Long plannedCents) {

    public YearMonth toMonth() {
        try {
            return YearMonth.parse(month);
        } catch (DateTimeParseException invalid) {
            throw new InvalidValueException("Le mois doit être au format AAAA-MM");
        }
    }
}
