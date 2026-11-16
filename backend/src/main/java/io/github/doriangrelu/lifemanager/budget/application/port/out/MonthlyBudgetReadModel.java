package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.BeneficiaryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ExpenseView;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.PayerTotal;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;

import java.time.YearMonth;
import java.util.List;
/**
 * Lectures de la vue mensuelle, calculées directement par la base (sommes groupées).
 * Une lecture n'a pas besoin de charger des agrégats : on interroge les données telles quelles.
 */
public interface MonthlyBudgetReadModel {

    /** Total par catégorie, du plus grand au plus petit. */
    List<CategoryTotal> totalsByCategory(HouseholdId householdId, YearMonth month);

    /** Total payé par chaque membre, du plus grand au plus petit. */
    List<PayerTotal> totalsByPayer(HouseholdId householdId, YearMonth month);

    /** Total dépensé pour chaque bénéficiaire (commun compris), du plus grand au plus petit. */
    List<BeneficiaryTotal> totalsByBeneficiary(HouseholdId householdId, YearMonth month);

    /** Dépenses du mois, la plus récente en premier. */
    List<ExpenseView> expenses(HouseholdId householdId, YearMonth month);
}
