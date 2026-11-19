package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.export;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.PersonalDataSource;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.export.SqlRows;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Export des données personnelles, domaine reminder : Rappels qu'il a saisis et rappels qu'il a marqués « c'est fait ». */
@Singleton
class ReminderPersonalData implements PersonalDataSource {

    private static final String RAPPELS = "SELECT r.household_id AS \"foyer\", r.title AS \"titre\", r.note AS \"note\", r.due_on AS"
            + " \"date\", r.recurrence AS \"recurrence\", r.notice AS \"prevenir\" FROM personal_reminder r JOIN"
            + " authorship a ON a.object_id = r.id WHERE a.member_id = ? ORDER BY r.due_on";

    private static final String FAITS = "SELECT d.household_id AS \"foyer\", d.title AS \"titre\", d.due_on AS \"prevuLe\", d.done_at AS"
            + " \"faitLe\" FROM reminder_done d WHERE d.done_by = ? ORDER BY d.done_at";

    private final JdbcOperations jdbc;

    ReminderPersonalData(@Named("reminder") JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String section() {
        return "rappels";
    }

    @Override
    public int order() {
        return 60;
    }

    @Override
    @Transactional(transactionManager = "reminder", readOnly = true)
    public Map<String, Object> exportFor(UUID memberId) {
        var data = new LinkedHashMap<String, Object>();
        data.put("rappels", SqlRows.query(jdbc, RAPPELS, memberId));
        data.put("faits", SqlRows.query(jdbc, FAITS, memberId));
        return data;
    }
}
