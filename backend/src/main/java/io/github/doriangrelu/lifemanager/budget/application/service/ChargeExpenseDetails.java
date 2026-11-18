package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordChargeExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.budget.domain.exception.UnusableCategoryException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Beneficiary;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseLabel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Détail de la dépense d'une échéance de charge : catégorie de la charge, ou « Autre » si elle n'est plus utilisable ;
 * bénéficiaire commun s'il a quitté le foyer. Vide si la dépense ne peut pas être créée (payeur parti, montant nul).
 */
@Singleton
public class ChargeExpenseDetails {

    private static final String FALLBACK_CATEGORY = "Autre";

    private final HouseholdMembers members;
    private final ExpenseCategoryResolver categories;

    ChargeExpenseDetails(HouseholdMembers members, ExpenseCategoryResolver categories) {
        this.members = members;
        this.categories = categories;
    }

    public Optional<ExpenseDetails> of(RecordChargeExpenseCommand command) {
        var household = new HouseholdId(command.householdId());
        var payer = new MemberId(command.payerId());
        if (command.amountCents() == 0 || !members.isMember(household, payer)) {
            return Optional.empty();
        }
        return Optional.of(new ExpenseDetails(Money.ofCents(command.amountCents()),
                category(household, command), command.paidOn(), payer, beneficiary(household, command),
                ExpenseLabel.ofNullable(command.chargeName())));
    }

    private CategoryId category(HouseholdId household, RecordChargeExpenseCommand command) {
        try {
            return categories.resolve(household, command.budgetCategoryId(), null);
        } catch (UnusableCategoryException archivedOrUnknown) {
            return categories.resolve(household, null, FALLBACK_CATEGORY);
        }
    }

    private Beneficiary beneficiary(HouseholdId household, RecordChargeExpenseCommand command) {
        if (command.beneficiaryId() == null) {
            return Beneficiary.SHARED;
        }
        var member = new MemberId(command.beneficiaryId());
        return members.isMember(household, member) ? new Beneficiary.Member(member) : Beneficiary.SHARED;
    }
}
