package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.export;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.PersonalDataSource;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.export.SqlRows;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Export des données personnelles, domaine vehicle : Véhicules qu'il a ajoutés, entretiens qu'il a notés, pièces jointes (sans leur contenu). */
@Singleton
class VehiclePersonalData implements PersonalDataSource {

    private static final String VEHICULES = "SELECT v.household_id AS \"foyer\", v.name AS \"nom\", v.license_plate AS \"immatriculation\","
            + " v.technical_inspection_due AS \"controleTechnique\", v.service_due AS \"revision\" FROM"
            + " vehicle v JOIN authorship a ON a.object_id = v.id WHERE a.member_id = ? ORDER BY v.name";

    private static final String ENTRETIENS = "SELECT v.household_id AS \"foyer\", v.name AS \"vehicule\", m.performed_on AS \"date\", m.kind"
            + " AS \"type\", m.other_label AS \"precision\", m.mileage_km AS \"kilometrage\", m.cost_cents AS"
            + " \"coutCentimes\", m.note AS \"note\", m.recorded_at AS \"saisiLe\" FROM maintenance_entry m JOIN"
            + " vehicle v ON v.id = m.vehicle_id WHERE m.recorded_by = ? ORDER BY m.performed_on";

    private static final String PIECESJOINTES = "SELECT v.household_id AS \"foyer\", v.name AS \"vehicule\", m.performed_on AS \"entretienDu\","
            + " f.file_name AS \"fichier\", f.media_type AS \"type\", f.size_bytes AS \"tailleOctets\","
            + " f.added_at AS \"ajouteeLe\" FROM maintenance_attachment f JOIN maintenance_entry m ON m.id ="
            + " f.entry_id JOIN vehicle v ON v.id = m.vehicle_id WHERE f.added_by = ? ORDER BY f.added_at";

    private final JdbcOperations jdbc;

    VehiclePersonalData(@Named("vehicle") JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String section() {
        return "vehicules";
    }

    @Override
    public int order() {
        return 40;
    }

    @Override
    @Transactional(transactionManager = "vehicle", readOnly = true)
    public Map<String, Object> exportFor(UUID memberId) {
        var data = new LinkedHashMap<String, Object>();
        data.put("vehicules", SqlRows.query(jdbc, VEHICULES, memberId));
        data.put("entretiens", SqlRows.query(jdbc, ENTRETIENS, memberId));
        data.put("piecesJointes", SqlRows.query(jdbc, PIECESJOINTES, memberId));
        return data;
    }
}
