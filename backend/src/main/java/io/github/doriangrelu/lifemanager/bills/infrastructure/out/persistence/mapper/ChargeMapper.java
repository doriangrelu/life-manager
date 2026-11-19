package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.bills.domain.aggregate.RecurringCharge;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Amount;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.AmountKind;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeDetails;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeName;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeSchedule;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.FlowDirection;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Frequency;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Loan;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity.RecurringChargeEntity;

/** Conversion entre l'agrégat {@link RecurringCharge} et sa ligne en base. */
public final class ChargeMapper {

    private ChargeMapper() {
    }

    public static RecurringChargeEntity toEntity(RecurringCharge charge) {
        var details = charge.details();
        var beneficiary = details.beneficiary();
        return new RecurringChargeEntity(charge.id().value(), charge.householdId().value(), details.name().value(),
                details.amount().cents(), details.kind().name(), charge.schedule().frequency().name(),
                charge.schedule().firstDueOn(), details.budgetCategoryId(), details.payer().value(),
                beneficiary == null ? null : beneficiary.value(), charge.active(), details.direction().name(),
                charge.schedule().lastDueOn(),
                details.optionalLoan().map(loan -> loan.insurance().cents()).orElse(null));
    }

    public static RecurringCharge toDomain(RecurringChargeEntity entity) {
        var details = new ChargeDetails(new ChargeName(entity.name()), Amount.ofCents(entity.amountCents()),
                AmountKind.of(entity.amountKind()), entity.budgetCategoryId(), new MemberId(entity.payerId()),
                entity.beneficiaryId() == null ? null : new MemberId(entity.beneficiaryId()),
                FlowDirection.of(entity.direction()), Loan.ofNullable(entity.loanInsuranceCents()));
        var schedule = new ChargeSchedule(entity.firstDueOn(), Frequency.of(entity.frequency()),
                entity.lastDueOn());
        return RecurringCharge.restore(new ChargeId(entity.id()), new HouseholdId(entity.householdId()), details,
                schedule, entity.active());
    }
}
