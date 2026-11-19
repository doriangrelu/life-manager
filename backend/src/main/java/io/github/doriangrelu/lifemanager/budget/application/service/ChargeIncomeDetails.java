package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordChargeExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.budget.domain.exception.UnusableCategoryException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Beneficiary;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseLabel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Détail de la rentrée d'une échéance de rentrée récurrente : catégorie de la charge, ou « Autre » (rentrée)
 * si elle n'est plus utilisable ; reçue par le foyer entier si le membre qui la recevait est parti.
 * Vide si le montant est nul ou si le foyer a été supprimé.
 */
@Singleton
public class ChargeIncomeDetails {

    private static final String FALLBACK_CATEGORY = "Autre";

    private final HouseholdMembers members;
    private final CategoryResolver categories;

    ChargeIncomeDetails(HouseholdMembers members, CategoryResolver categories) {
        this.members = members;
        this.categories = categories;
    }

    public Optional<IncomeDetails> of(RecordChargeExpenseCommand command) {
        var household = new HouseholdId(command.householdId());
        // Foyer supprimé entre-temps (événement arrivé en retard) : rien à créer.
        if (command.amountCents() == 0 || !members.isKnown(household)) {
            return Optional.empty();
        }
        return Optional.of(new IncomeDetails(Money.ofCents(command.amountCents()), category(household, command),
                command.paidOn(), receiver(household, command), ExpenseLabel.ofNullable(command.chargeName())));
    }

    private CategoryId category(HouseholdId household, RecordChargeExpenseCommand command) {
        try {
            return categories.resolve(household, CategoryKind.INCOME, command.budgetCategoryId(), null);
        } catch (UnusableCategoryException archivedOrUnknown) {
            return categories.resolve(household, CategoryKind.INCOME, null, FALLBACK_CATEGORY);
        }
    }

    private Beneficiary receiver(HouseholdId household, RecordChargeExpenseCommand command) {
        var member = new MemberId(command.payerId());
        return members.isMember(household, member) ? new Beneficiary.Member(member) : Beneficiary.SHARED;
    }
}
