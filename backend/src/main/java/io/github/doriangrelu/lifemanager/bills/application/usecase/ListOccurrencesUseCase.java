package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.ListOccurrences;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.OccurrenceView;
import io.github.doriangrelu.lifemanager.bills.application.port.out.OccurrenceRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.github.doriangrelu.lifemanager.bills.application.service.OpenOccurrences;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Liste les échéances d'une charge, de la plus récente à la plus ancienne. */
@Singleton
class ListOccurrencesUseCase implements ListOccurrences {

    private final ChargeAccess access;
    private final OccurrenceRepository occurrences;
    private final OpenOccurrences open;

    ListOccurrencesUseCase(ChargeAccess access, OccurrenceRepository occurrences, OpenOccurrences open) {
        this.access = access;
        this.occurrences = occurrences;
        this.open = open;
    }

    @Override
    @Transactional(transactionManager = "bills", readOnly = true)
    public List<OccurrenceView> list(ChargeCommand command) {
        var charge = access.chargeForMember(command.householdId(), command.chargeId(), command.requesterId());
        return occurrences.findAllByCharge(charge.id()).stream().map(open::view).toList();
    }
}
