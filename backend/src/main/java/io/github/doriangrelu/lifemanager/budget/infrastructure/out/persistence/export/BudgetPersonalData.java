package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.export;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.PersonalDataSource;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.export.SqlRows;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Export des données personnelles, domaine budget : Dépenses et rentrées qu'il a saisies, catégories qu'il a créées et leurs budgets prévus. */
@Singleton
class BudgetPersonalData implements PersonalDataSource {

    private static final String DEPENSES = "SELECT e.household_id AS \"foyer\", e.spent_on AS \"date\", e.amount_cents AS"
            + " \"montantCentimes\", c.name AS \"categorie\", e.label AS \"libelle\", (e.paid_by = ?) AS"
            + " \"payeeParMoi\", e.recorded_at AS \"saisieLe\" FROM expense e JOIN category c ON c.id ="
            + " e.category_id WHERE e.recorded_by = ? ORDER BY e.spent_on, e.recorded_at";

    private static final String RENTREES = "SELECT i.household_id AS \"foyer\", i.received_on AS \"date\", i.amount_cents AS"
            + " \"montantCentimes\", c.name AS \"categorie\", i.label AS \"libelle\", (i.received_by = ?) AS"
            + " \"recueParMoi\", i.recorded_at AS \"saisieLe\" FROM income i JOIN category c ON c.id ="
            + " i.category_id WHERE i.recorded_by = ? ORDER BY i.received_on, i.recorded_at";

    private static final String CATEGORIES = "SELECT c.household_id AS \"foyer\", c.name AS \"nom\", c.kind AS \"sens\", c.archived AS"
            + " \"archivee\" FROM category c JOIN authorship a ON a.object_id = c.id WHERE a.member_id = ?"
            + " ORDER BY c.name";

    private static final String BUDGETSPREVUS = "SELECT b.household_id AS \"foyer\", c.name AS \"categorie\", b.month AS \"mois\", b.scope AS"
            + " \"portee\", b.planned_cents AS \"prevuCentimes\" FROM category_budget b JOIN category c ON"
            + " c.id = b.category_id JOIN authorship a ON a.object_id = c.id WHERE a.member_id = ? ORDER"
            + " BY c.name, b.month";

    private final JdbcOperations jdbc;

    BudgetPersonalData(@Named("budget") JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String section() {
        return "budget";
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    @Transactional(transactionManager = "budget", readOnly = true)
    public Map<String, Object> exportFor(UUID memberId) {
        var data = new LinkedHashMap<String, Object>();
        data.put("depenses", SqlRows.query(jdbc, DEPENSES, memberId));
        data.put("rentrees", SqlRows.query(jdbc, RENTREES, memberId));
        data.put("categories", SqlRows.query(jdbc, CATEGORIES, memberId));
        data.put("budgetsPrevus", SqlRows.query(jdbc, BUDGETSPREVUS, memberId));
        return data;
    }
}
