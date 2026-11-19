-- Budget prévu par catégorie : des règles datées (les mois passés gardent leur prévu).
-- scope = FROM_MONTH : vaut à partir de ce mois ; MONTH_ONLY : pour ce mois seulement.
-- planned_cents absent : la règle retire le prévu.
CREATE TABLE category_budget
(
    id            UUID PRIMARY KEY,
    household_id  UUID        NOT NULL,
    category_id   UUID        NOT NULL REFERENCES category (id) ON DELETE CASCADE,
    month         DATE        NOT NULL,
    scope         VARCHAR(12) NOT NULL,
    planned_cents BIGINT CHECK (planned_cents > 0),
    UNIQUE (category_id, month, scope)
);
CREATE INDEX category_budget_household ON category_budget (household_id, month);
