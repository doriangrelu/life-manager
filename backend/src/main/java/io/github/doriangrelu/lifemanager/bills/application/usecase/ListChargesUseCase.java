package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.ListCharges;
import io.github.doriangrelu.lifemanager.bills.application.port.in.query.ListChargesQuery;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ChargeView;
import io.github.doriangrelu.lifemanager.bills.application.port.out.ChargeRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.github.doriangrelu.lifemanager.bills.application.service.OpenOccurrences;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Liste les charges du foyer avec leur prochaine échéance. */
@Singleton
class ListChargesUseCase implements ListCharges {

    private final ChargeAccess access;
    private final ChargeRepository charges;
    private final OpenOccurrences open;

    ListChargesUseCase(ChargeAccess access, ChargeRepository charges, OpenOccurrences open) {
        this.access = access;
        this.charges = charges;
        this.open = open;
    }

    @Override
    @Transactional(transactionManager = "bills", readOnly = true)
    public List<ChargeView> list(ListChargesQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        return open.viewsOf(household, charges.findAllByHousehold(household));
    }
}
