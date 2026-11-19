package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.export;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.PersonalDataSource;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.export.SqlRows;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Export des données personnelles, domaine bills : Récurrents qu'il a créés ou qu'il paie, factures qu'il a jointes (sans leur contenu). */
@Singleton
class BillsPersonalData implements PersonalDataSource {

    private static final String RECURRENTS = "SELECT r.household_id AS \"foyer\", r.name AS \"nom\", r.direction AS \"sens\", r.amount_cents"
            + " AS \"montantCentimes\", r.amount_kind AS \"typeMontant\", r.frequency AS \"frequence\","
            + " r.first_due_on AS \"premiereEcheance\", r.last_due_on AS \"derniereEcheance\","
            + " r.loan_insurance_cents AS \"assuranceCentimes\", r.active AS \"actif\", (a.member_id IS NOT"
            + " NULL) AS \"creeParMoi\", (r.payer_id = ?) AS \"payeParMoi\" FROM recurring_charge r LEFT JOIN"
            + " authorship a ON a.object_id = r.id AND a.member_id = ? WHERE r.payer_id = ? OR a.member_id"
            + " IS NOT NULL ORDER BY r.name";

    private static final String FACTURES = "SELECT r.household_id AS \"foyer\", r.name AS \"recurrent\", o.due_on AS \"echeance\","
            + " i.file_name AS \"fichier\", i.media_type AS \"type\", i.size_bytes AS \"tailleOctets\","
            + " i.added_at AS \"ajouteeLe\" FROM invoice i JOIN charge_occurrence o ON o.id ="
            + " i.occurrence_id JOIN recurring_charge r ON r.id = o.charge_id WHERE i.added_by = ? ORDER"
            + " BY i.added_at";

    private final JdbcOperations jdbc;

    BillsPersonalData(@Named("bills") JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String section() {
        return "recurrents";
    }

    @Override
    public int order() {
        return 30;
    }

    @Override
    @Transactional(transactionManager = "bills", readOnly = true)
    public Map<String, Object> exportFor(UUID memberId) {
        var data = new LinkedHashMap<String, Object>();
        data.put("recurrents", SqlRows.query(jdbc, RECURRENTS, memberId));
        data.put("factures", SqlRows.query(jdbc, FACTURES, memberId));
        return data;
    }
}
