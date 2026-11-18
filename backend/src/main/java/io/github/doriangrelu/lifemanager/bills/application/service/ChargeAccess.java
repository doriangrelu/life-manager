package io.github.doriangrelu.lifemanager.bills.application.service;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.InvoiceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.OccurrenceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.out.ChargeRepository;
import io.github.doriangrelu.lifemanager.bills.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceRepository;
import io.github.doriangrelu.lifemanager.bills.application.port.out.OccurrenceRepository;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.ChargeOccurrence;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.Invoice;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.RecurringCharge;
import io.github.doriangrelu.lifemanager.bills.domain.exception.BillsHouseholdNotFoundException;
import io.github.doriangrelu.lifemanager.bills.domain.exception.ChargeNotFoundException;
import io.github.doriangrelu.lifemanager.bills.domain.exception.InvoiceNotFoundException;
import io.github.doriangrelu.lifemanager.bills.domain.exception.OccurrenceNotFoundException;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.InvoiceId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;
import jakarta.inject.Singleton;

import java.util.UUID;

/**
 * Cloisonnement du domaine des charges : seuls les membres d'un foyer accèdent à ses charges, à leurs échéances
 * et à leurs factures. Tout use case qui cible un foyer passe par ici ; un objet d'un autre foyer est « introuvable ».
 */
@Singleton
public class ChargeAccess {

    private final HouseholdMembers members;
    private final ChargeRepository charges;
    private final OccurrenceRepository occurrences;
    private final InvoiceRepository invoices;

    ChargeAccess(HouseholdMembers members, ChargeRepository charges, OccurrenceRepository occurrences,
                 InvoiceRepository invoices) {
        this.members = members;
        this.charges = charges;
        this.occurrences = occurrences;
        this.invoices = invoices;
    }

    /** @throws BillsHouseholdNotFoundException si le demandeur n'est pas membre du foyer */
    public HouseholdId householdOfMember(UUID householdId, String requesterId) {
        var household = new HouseholdId(householdId);
        if (!members.isMember(household, MemberId.of(requesterId))) {
            throw new BillsHouseholdNotFoundException();
        }
        return household;
    }

    /** @throws ChargeNotFoundException si la charge n'existe pas dans ce foyer */
    public RecurringCharge chargeForMember(UUID householdId, UUID chargeId, String requesterId) {
        var household = householdOfMember(householdId, requesterId);
        return charges.findById(new ChargeId(chargeId))
                .filter(charge -> charge.belongsTo(household))
                .orElseThrow(ChargeNotFoundException::new);
    }

    /** @throws OccurrenceNotFoundException si l'échéance n'appartient pas à cette charge */
    public ChargeOccurrence occurrenceForMember(OccurrenceCommand command) {
        var charge = chargeForMember(command.householdId(), command.chargeId(), command.requesterId());
        return occurrences.findById(new OccurrenceId(command.occurrenceId()))
                .filter(occurrence -> occurrence.belongsTo(charge.id()))
                .orElseThrow(OccurrenceNotFoundException::new);
    }

    /** @throws InvoiceNotFoundException si la facture n'appartient pas à cette échéance */
    public Invoice invoiceForMember(InvoiceCommand command) {
        var occurrence = occurrenceForMember(command.target());
        return invoices.findById(new InvoiceId(command.invoiceId()))
                .filter(invoice -> invoice.belongsTo(occurrence.id()))
                .orElseThrow(InvoiceNotFoundException::new);
    }
}
