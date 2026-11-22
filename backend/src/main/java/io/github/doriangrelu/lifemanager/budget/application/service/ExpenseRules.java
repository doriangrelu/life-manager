package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.ExpenseInput;
import io.github.doriangrelu.lifemanager.budget.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ProjectRefs;
import io.github.doriangrelu.lifemanager.budget.domain.exception.NotAHouseholdMemberException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Beneficiary;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseLabel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectRef;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import jakarta.inject.Singleton;

/**
 * Règles d'une dépense qui concernent d'autres objets que la dépense elle-même,
 * partagées par la saisie et la correction : catégorie utilisable (ou créée à la volée),
 * payeur et bénéficiaire membres du foyer, projet en cours du foyer.
 */
@Singleton
public class ExpenseRules {

    private final CategoryResolver categoryResolver;
    private final HouseholdMembers members;
    private final ProjectRefs projects;

    ExpenseRules(CategoryResolver categoryResolver, HouseholdMembers members, ProjectRefs projects) {
        this.categoryResolver = categoryResolver;
        this.members = members;
        this.projects = projects;
    }

    /** Saisie d'une nouvelle dépense. */
    public ExpenseDetails validDetails(HouseholdId householdId, ExpenseInput input) {
        return validDetails(householdId, input, null);
    }

    /**
     * Transforme la saisie en {@link ExpenseDetails} et vérifie qu'elle est acceptable pour ce foyer.
     *
     * @param currentProject projet déjà rattaché (correction) : il reste accepté même s'il est terminé
     * @throws NotAHouseholdMemberException si le payeur ou le bénéficiaire n'est pas membre du foyer
     */
    public ExpenseDetails validDetails(HouseholdId householdId, ExpenseInput input, ProjectId currentProject) {
        var categoryId = categoryResolver.resolve(householdId, CategoryKind.EXPENSE, input.categoryId(),
                input.newCategoryName());
        var details = new ExpenseDetails(
                Money.ofCents(input.amountCents()),
                categoryId,
                input.spentOn(),
                new MemberId(input.paidBy()),
                Beneficiary.of(input.beneficiary()),
                ExpenseLabel.ofNullable(input.label()),
                project(householdId, ProjectId.ofNullable(input.projectId()), currentProject));
        ensureMember(householdId, details.paidBy());
        details.beneficiary().member().ifPresent(member -> ensureMember(householdId, member));
        return details;
    }

    /** Un projet inconnu, d'un autre foyer, ou clos (sauf s'il était déjà rattaché) est refusé. */
    private ProjectId project(HouseholdId householdId, ProjectId wanted, ProjectId current) {
        if (wanted == null) {
            return null;
        }
        var known = projects.find(householdId, wanted);
        if (known.isEmpty() || !known.get().active() && !wanted.equals(current)) {
            throw new InvalidValueException("Le projet doit être un projet en cours du foyer");
        }
        return known.map(ProjectRef::id).orElseThrow();
    }

    private void ensureMember(HouseholdId householdId, MemberId memberId) {
        if (!members.isMember(householdId, memberId)) {
            throw new NotAHouseholdMemberException();
        }
    }
}
