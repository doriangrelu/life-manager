package io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.export;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.PersonalDataSource;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.export.SqlRows;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Export des données personnelles, domaine project : projets qu'il a créés et tâches qui lui sont confiées. */
@Singleton
class ProjectPersonalData implements PersonalDataSource {

    private static final String PROJETS = "SELECT p.household_id AS \"foyer\", p.name AS \"nom\", p.description AS"
            + " \"description\", p.planned_cents AS \"budgetPrevuCentimes\", p.start_on AS \"debut\", p.target_on AS"
            + " \"finVisee\", p.status AS \"etat\" FROM project p JOIN authorship a ON a.object_id = p.id"
            + " WHERE a.member_id = ? ORDER BY p.created_at";

    private static final String TACHES = "SELECT t.household_id AS \"foyer\", t.title AS \"titre\", t.due_on AS"
            + " \"date\", t.done_at AS \"faiteLe\" FROM project_task t WHERE t.assignee_id = ? ORDER BY t.due_on";

    private final JdbcOperations jdbc;

    ProjectPersonalData(@Named("project") JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String section() {
        return "projets";
    }

    @Override
    public int order() {
        return 70;
    }

    @Override
    @Transactional(transactionManager = "project", readOnly = true)
    public Map<String, Object> exportFor(UUID memberId) {
        var data = new LinkedHashMap<String, Object>();
        data.put("projets", SqlRows.query(jdbc, PROJETS, memberId));
        data.put("tachesConfiees", SqlRows.query(jdbc, TACHES, memberId));
        return data;
    }
}
