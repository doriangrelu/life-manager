package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.IncomeInput;
import io.github.doriangrelu.lifemanager.budget.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.budget.domain.exception.NotAHouseholdMemberException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Beneficiary;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseLabel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;
import jakarta.inject.Singleton;

/**
 * Règles d'une rentrée qui concernent d'autres objets qu'elle, partagées par la saisie et la correction :
 * catégorie de rentrée utilisable (ou créée à la volée), bénéficiaire membre du foyer.
 */
@Singleton
public class IncomeRules {

    private final CategoryResolver categoryResolver;
    private final HouseholdMembers members;

    IncomeRules(CategoryResolver categoryResolver, HouseholdMembers members) {
        this.categoryResolver = categoryResolver;
        this.members = members;
    }

    /** @throws NotAHouseholdMemberException si le bénéficiaire n'est pas membre du foyer */
    public IncomeDetails validDetails(HouseholdId householdId, IncomeInput input) {
        var categoryId = categoryResolver.resolve(householdId, CategoryKind.INCOME, input.categoryId(),
                input.newCategoryName());
        var details = new IncomeDetails(
                Money.ofCents(input.amountCents()),
                categoryId,
                input.receivedOn(),
                Beneficiary.of(input.receivedBy()),
                ExpenseLabel.ofNullable(input.label()));
        details.receivedBy().member()
                .filter(member -> !members.isMember(householdId, member))
                .ifPresent(stranger -> {
                    throw new NotAHouseholdMemberException();
                });
        return details;
    }
}
