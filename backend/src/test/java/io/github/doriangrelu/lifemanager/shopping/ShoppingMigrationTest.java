package io.github.doriangrelu.lifemanager.shopping;

import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** La migration V2 range les articles d'une liste unique (V1) dans la sous-liste « Courses » de leur foyer. */
class ShoppingMigrationTest extends IntegrationTestSupport {

    // Schéma à part : celui de l'application est déjà migré en dernière version.
    private static final String SCHEMA = "shopping_migration_test";

    @Test
    void existing_items_move_to_the_groceries_list() throws SQLException {
        var household = UUID.randomUUID();
        migrateTo("1");
        try (var connection = connect()) {
            execute(connection, "INSERT INTO household_member VALUES ('%s', '%s', '%s')"
                    .formatted(UUID.randomUUID(), household, UUID.randomUUID()));
            execute(connection, "INSERT INTO shopping_item VALUES ('%s', '%s', 'Lait', NULL, '%s', now())"
                    .formatted(UUID.randomUUID(), household, UUID.randomUUID()));
        }

        migrateTo("latest");

        try (var connection = connect()) {
            assertThat(strings(connection, "SELECT name FROM shopping_list ORDER BY name"))
                    .containsExactly("Bricolage", "Courses", "Vêtements");
            assertThat(strings(connection, """
                    SELECT list.name || ':' || item.label FROM shopping_item item
                    JOIN shopping_list list ON list.id = item.list_id""")).containsExactly("Courses:Lait");
        }
    }

    private static void migrateTo(String version) {
        Flyway.configure()
                .dataSource(postgres().getJdbcUrl(), postgres().getUsername(), postgres().getPassword())
                .schemas(SCHEMA)
                .locations("classpath:db/migration/shopping")
                .target(version)
                .load()
                .migrate();
    }

    private static Connection connect() throws SQLException {
        var connection = DriverManager.getConnection(postgres().getJdbcUrl(), postgres().getUsername(),
                postgres().getPassword());
        connection.setSchema(SCHEMA);
        return connection;
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static List<String> strings(Connection connection, String sql) throws SQLException {
        var values = new ArrayList<String>();
        try (var statement = connection.createStatement(); var rows = statement.executeQuery(sql)) {
            while (rows.next()) {
                values.add(rows.getString(1));
            }
        }
        return values;
    }
}
