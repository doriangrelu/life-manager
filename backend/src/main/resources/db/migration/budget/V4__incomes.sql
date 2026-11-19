-- Suivi des rentrées d'argent (lot 4a). Rien ne change pour les dépenses existantes.

-- Sens d'une catégorie : EXPENSE (dépenses, toutes les catégories existantes) ou INCOME (rentrées).
ALTER TABLE category ADD COLUMN kind VARCHAR(10) NOT NULL DEFAULT 'EXPENSE'
    CHECK (kind IN ('EXPENSE', 'INCOME'));

-- Un nom n'est unique que pour un même sens : « Autre » existe en dépense et en rentrée.
DROP INDEX category_name_per_household;
CREATE UNIQUE INDEX category_name_per_household ON category (household_id, kind, lower(name));

CREATE TABLE income
(
    id           UUID PRIMARY KEY,
    household_id UUID        NOT NULL,
    category_id  UUID        NOT NULL REFERENCES category (id),
    amount_cents BIGINT      NOT NULL CHECK (amount_cents > 0),
    received_on  DATE        NOT NULL,
    received_by  UUID, -- NULL = rentrée du foyer entier
    label        VARCHAR(140),
    recorded_by  UUID        NOT NULL,
    recorded_at  TIMESTAMPTZ NOT NULL
);
CREATE INDEX income_household_month ON income (household_id, received_on);

-- Échéance de rentrée récurrente → rentrée qu'elle a produite (même principe que charge_expense).
CREATE TABLE charge_income
(
    occurrence_id UUID PRIMARY KEY,
    income_id     UUID NOT NULL
);
