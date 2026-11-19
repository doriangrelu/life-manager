package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.BeneficiaryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthCategoryAmount;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthResult;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;

import java.time.LocalDate;
import java.util.List;

/** Lectures du bilan d'une période (jours inclus), calculées par la base. */
public interface BudgetReportReadModel {

    /** Rentrées par catégorie, de la plus grosse à la plus petite. */
    List<CategoryTotal> incomeByCategory(HouseholdId householdId, LocalDate firstDay, LocalDate lastDay);

    /** Rentrées par personne qui les reçoit ({@code null} = foyer entier). */
    List<BeneficiaryTotal> incomeByReceiver(HouseholdId householdId, LocalDate firstDay, LocalDate lastDay);

    /** Dépenses par catégorie, de la plus grosse à la plus petite. */
    List<CategoryTotal> expenseByCategory(HouseholdId householdId, LocalDate firstDay, LocalDate lastDay);

    /** Total des dépenses nées d'une dépense récurrente (charge payée), en centimes. */
    long recurringExpenseCents(HouseholdId householdId, LocalDate firstDay, LocalDate lastDay);

    /** Dépenses de chaque catégorie, mois par mois. */
    List<MonthCategoryAmount> expensesByMonthAndCategory(HouseholdId householdId, LocalDate firstDay,
                                                         LocalDate lastDay);

    /** Rentrées et dépenses mois par mois (seuls les mois qui en ont). */
    List<MonthResult> months(HouseholdId householdId, LocalDate firstDay, LocalDate lastDay);
}
