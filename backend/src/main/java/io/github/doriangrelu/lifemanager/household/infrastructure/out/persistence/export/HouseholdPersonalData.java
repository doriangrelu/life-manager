package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.export;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.PersonalDataSource;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.export.SqlRows;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Export des données personnelles, domaine household : Foyers dont il est membre : nom, rôle, date d'arrivée. */
@Singleton
class HouseholdPersonalData implements PersonalDataSource {

    private static final String APPARTENANCES = "SELECT m.household_id AS \"foyer\", h.name AS \"nom\", CASE WHEN h.owner_id = m.member_id THEN"
            + " 'propriétaire' ELSE 'membre' END AS \"role\", m.joined_at AS \"arriveLe\" FROM membership m"
            + " JOIN household h ON h.id = m.household_id WHERE m.member_id = ? ORDER BY m.joined_at";

    private final JdbcOperations jdbc;

    HouseholdPersonalData(@Named("household") JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String section() {
        return "foyers";
    }

    @Override
    public int order() {
        return 10;
    }

    @Override
    @Transactional(transactionManager = "household", readOnly = true)
    public Map<String, Object> exportFor(UUID memberId) {
        var data = new LinkedHashMap<String, Object>();
        data.put("appartenances", SqlRows.query(jdbc, APPARTENANCES, memberId));
        return data;
    }
}
