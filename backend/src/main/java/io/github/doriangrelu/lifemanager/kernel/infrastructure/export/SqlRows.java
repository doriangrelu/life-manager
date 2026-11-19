package io.github.doriangrelu.lifemanager.kernel.infrastructure.export;

import io.micronaut.data.jdbc.runtime.JdbcOperations;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Lecture SQL pour l'export des données personnelles : chaque ligne devient un tableau
 * « nom de colonne → valeur simple » (dates et identifiants en texte), dans l'ordre des colonnes.
 * À appeler dans une transaction de lecture de la datasource du domaine.
 */
public final class SqlRows {

    private SqlRows() {
    }

    /** Exécute la requête ; chaque {@code ?} reçoit l'identifiant du membre. */
    public static List<Map<String, Object>> query(JdbcOperations jdbc, String sql, UUID memberId) {
        return jdbc.prepareStatement(sql, statement -> {
            try {
                bindMember(statement, memberId);
                try (var rows = statement.executeQuery()) {
                    return readAll(rows);
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Export des données impossible", e);
            }
        });
    }

    private static void bindMember(PreparedStatement statement, UUID memberId) throws SQLException {
        var count = statement.getParameterMetaData().getParameterCount();
        for (int i = 1; i <= count; i++) {
            statement.setObject(i, memberId);
        }
    }

    private static List<Map<String, Object>> readAll(ResultSet rows) throws SQLException {
        var meta = rows.getMetaData();
        var result = new ArrayList<Map<String, Object>>();
        while (rows.next()) {
            var row = new LinkedHashMap<String, Object>();
            for (int column = 1; column <= meta.getColumnCount(); column++) {
                row.put(meta.getColumnLabel(column), simple(rows.getObject(column)));
            }
            result.add(row);
        }
        return result;
    }

    // Valeurs que le JSON sait écrire sans réglage : texte, nombres, booléens.
    private static Object simple(Object value) {
        return switch (value) {
            case null -> null;
            case Date date -> date.toLocalDate().toString();
            case Timestamp timestamp -> timestamp.toInstant().toString();
            case UUID id -> id.toString();
            case String text -> text;
            case Number number -> number.longValue();
            case Boolean flag -> flag;
            default -> value.toString();
        };
    }
}
