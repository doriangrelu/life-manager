package io.github.doriangrelu.lifemanager.bills.application.service;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ChargeInput;
import io.github.doriangrelu.lifemanager.bills.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.bills.domain.exception.NotAHouseholdMemberException;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Amount;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.AmountKind;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeDetails;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeName;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeSchedule;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.FlowDirection;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Frequency;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Loan;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.MemberId;
import jakarta.inject.Singleton;

/** Transforme la saisie d'une charge en objets du domaine, partagée par l'ajout et la modification. */
@Singleton
public class ChargeInputs {

    private final HouseholdMembers members;

    ChargeInputs(HouseholdMembers members) {
        this.members = members;
    }

    /** @throws NotAHouseholdMemberException si le payeur ou le bénéficiaire n'est pas membre du foyer */
    public ChargeDetails details(HouseholdId householdId, ChargeInput input) {
        var payer = new MemberId(input.payerId());
        var beneficiary = input.beneficiaryId() == null ? null : new MemberId(input.beneficiaryId());
        ensureMember(householdId, payer);
        if (beneficiary != null) {
            ensureMember(householdId, beneficiary);
        }
        return new ChargeDetails(new ChargeName(input.name()), Amount.ofCents(input.amountCents()),
                AmountKind.of(input.amountKind()), input.budgetCategoryId(), payer, beneficiary,
                FlowDirection.of(input.direction()), Loan.ofNullable(input.loanInsuranceCents()));
    }

    public static ChargeSchedule schedule(ChargeInput input) {
        return new ChargeSchedule(input.firstDueOn(), Frequency.of(input.frequency()), input.lastDueOn());
    }

    private void ensureMember(HouseholdId householdId, MemberId memberId) {
        if (!members.isMember(householdId, memberId)) {
            throw new NotAHouseholdMemberException();
        }
    }
}
