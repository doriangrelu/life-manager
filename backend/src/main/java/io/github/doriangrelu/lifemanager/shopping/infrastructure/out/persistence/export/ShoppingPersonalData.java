package io.github.doriangrelu.lifemanager.shopping.infrastructure.out.persistence.export;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.PersonalDataSource;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.export.SqlRows;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Export des données personnelles, domaine shopping : Listes qu'il a créées et articles qu'il a ajoutés. */
@Singleton
class ShoppingPersonalData implements PersonalDataSource {

    private static final String LISTES = "SELECT l.household_id AS \"foyer\", l.name AS \"nom\" FROM shopping_list l JOIN authorship a"
            + " ON a.object_id = l.id WHERE a.member_id = ? ORDER BY l.name";

    private static final String ARTICLES = "SELECT l.household_id AS \"foyer\", l.name AS \"liste\", i.label AS \"article\", (i.checked_at"
            + " IS NOT NULL) AS \"coche\", i.added_at AS \"ajouteLe\" FROM shopping_item i JOIN shopping_list"
            + " l ON l.id = i.list_id WHERE i.added_by = ? ORDER BY i.added_at";

    private final JdbcOperations jdbc;

    ShoppingPersonalData(@Named("shopping") JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String section() {
        return "courses";
    }

    @Override
    public int order() {
        return 50;
    }

    @Override
    @Transactional(transactionManager = "shopping", readOnly = true)
    public Map<String, Object> exportFor(UUID memberId) {
        var data = new LinkedHashMap<String, Object>();
        data.put("listes", SqlRows.query(jdbc, LISTES, memberId));
        data.put("articles", SqlRows.query(jdbc, ARTICLES, memberId));
        return data;
    }
}
