package io.github.doriangrelu.lifemanager.stabilisation;

import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordChargeIncome;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordChargeExpenseCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.TrackChargeDeadline;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.TrackChargeDeadlineCommand;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Événements arrivés en retard (boîte d'envoi d'un autre domaine) pour un foyer déjà supprimé :
 * ils ne doivent rien recréer, sinon ces données ne seraient plus jamais effacées.
 */
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LateEventsIntegrationTest extends IntegrationTestSupport {

    @Inject
    RecordChargeIncome recordChargeIncome;

    @Inject
    TrackChargeDeadline trackChargeDeadline;

    @Test
    void a_late_recurring_income_of_a_deleted_household_creates_nothing() throws SQLException {
        var gone = UUID.randomUUID();

        recordChargeIncome.record(new RecordChargeExpenseCommand(UUID.randomUUID(), gone, "Salaire", 200000,
                LocalDate.now(), UUID.randomUUID(), UUID.randomUUID(), null));

        assertThat(count("SELECT count(*) FROM budget.income WHERE household_id = ?", gone)).isZero();
        assertThat(count("SELECT count(*) FROM budget.category WHERE household_id = ?", gone)).isZero();
    }

    @Test
    void a_late_charge_deadline_of_a_deleted_household_creates_nothing() throws SQLException {
        var gone = UUID.randomUUID();

        trackChargeDeadline.track(new TrackChargeDeadlineCommand(UUID.randomUUID(), gone, "Internet",
                LocalDate.now().plusDays(3), false));

        assertThat(count("SELECT count(*) FROM reminder.deadline WHERE household_id = ?", gone)).isZero();
    }

    private static long count(String sql, UUID id) throws SQLException {
        try (var connection = DriverManager.getConnection(postgres().getJdbcUrl(), postgres().getUsername(),
                postgres().getPassword()); var statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (var result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }
}
