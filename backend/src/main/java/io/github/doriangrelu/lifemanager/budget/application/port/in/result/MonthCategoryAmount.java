package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import java.time.YearMonth;
import java.util.UUID;

/** Un montant en centimes pour une catégorie et un mois : son budget prévu, ou ce qui y a été dépensé. */
public record MonthCategoryAmount(YearMonth month, UUID categoryId, long cents) {
}
