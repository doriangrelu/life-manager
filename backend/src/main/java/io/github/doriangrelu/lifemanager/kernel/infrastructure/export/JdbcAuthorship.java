package io.github.doriangrelu.lifemanager.kernel.infrastructure.export;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.Authorship;
import io.micronaut.data.jdbc.runtime.JdbcOperations;

import java.sql.SQLException;
import java.util.UUID;

/** Table {@code authorship} d'un domaine (une instance par domaine, voir les fabriques). */
public final class JdbcAuthorship implements Authorship {

    private static final String UPSERT = "INSERT INTO authorship (object_id, household_id, member_id) VALUES (?, ?, ?)"
            + " ON CONFLICT (object_id) DO UPDATE SET member_id = EXCLUDED.member_id";

    private final JdbcOperations jdbc;

    public JdbcAuthorship(JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void record(UUID objectId, UUID householdId, UUID memberId) {
        jdbc.prepareStatement(UPSERT, statement -> {
            try {
                statement.setObject(1, objectId);
                statement.setObject(2, householdId);
                statement.setObject(3, memberId);
                return statement.executeUpdate();
            } catch (SQLException e) {
                throw new IllegalStateException("Auteur non enregistré", e);
            }
        });
    }
}
