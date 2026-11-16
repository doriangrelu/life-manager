package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.ExpenseInput;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.budget.domain.exception.NotAHouseholdMemberException;
import io.github.doriangrelu.lifemanager.budget.domain.exception.UnusableCategoryException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Beneficiary;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseLabel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;
import jakarta.inject.Singleton;

/**
 * Règles d'une dépense qui concernent d'autres objets que la dépense elle-même,
 * partagées par la saisie et la correction : catégorie utilisable, payeur et bénéficiaire membres du foyer.
 */
@Singleton
public class ExpenseRules {

    private final CategoryRepository categories;
    private final HouseholdMembers members;

    ExpenseRules(CategoryRepository categories, HouseholdMembers members) {
        this.categories = categories;
        this.members = members;
    }

    /**
     * Transforme la saisie en {@link ExpenseDetails} et vérifie qu'elle est acceptable pour ce foyer.
     *
     * @throws UnusableCategoryException     si la catégorie est archivée ou d'un autre foyer
     * @throws NotAHouseholdMemberException  si le payeur ou le bénéficiaire n'est pas membre du foyer
     */
    public ExpenseDetails validDetails(HouseholdId householdId, ExpenseInput input) {
        var details = new ExpenseDetails(
                Money.ofCents(input.amountCents()),
                new CategoryId(input.categoryId()),
                input.spentOn(),
                new MemberId(input.paidBy()),
                Beneficiary.of(input.beneficiary()),
                ExpenseLabel.ofNullable(input.label()));
        ensureCategoryUsable(householdId, details.categoryId());
        ensureMember(householdId, details.paidBy());
        details.beneficiary().member().ifPresent(member -> ensureMember(householdId, member));
        return details;
    }

    private void ensureCategoryUsable(HouseholdId householdId, CategoryId categoryId) {
        if (categories.findById(categoryId).filter(category -> category.isUsableBy(householdId)).isEmpty()) {
            throw new UnusableCategoryException();
        }
    }

    private void ensureMember(HouseholdId householdId, MemberId memberId) {
        if (!members.isMember(householdId, memberId)) {
            throw new NotAHouseholdMemberException();
        }
    }
}
