-- Tables du domaine budget (schéma budget). Aucune référence vers un autre schéma.
-- Montants en centimes d'euro (BIGINT), jamais de nombre à virgule.

-- Copie locale des membres de chaque foyer, alimentée par les événements du domaine foyer.
CREATE TABLE household_member
(
    id           UUID PRIMARY KEY,
    household_id UUID NOT NULL,
    member_id    UUID NOT NULL,
    CONSTRAINT household_member_once UNIQUE (household_id, member_id)
);

CREATE TABLE category
(
    id           UUID PRIMARY KEY,
    household_id UUID        NOT NULL,
    name         VARCHAR(40) NOT NULL,
    archived     BOOLEAN     NOT NULL
);
-- Nom unique dans un foyer, sans tenir compte des majuscules.
CREATE UNIQUE INDEX category_name_per_household ON category (household_id, lower(name));

CREATE TABLE expense
(
    id           UUID PRIMARY KEY,
    household_id UUID         NOT NULL,
    category_id  UUID         NOT NULL REFERENCES category (id),
    amount_cents BIGINT       NOT NULL CHECK (amount_cents > 0),
    spent_on     DATE         NOT NULL,
    paid_by      UUID         NOT NULL,
    beneficiary  UUID, -- NULL = dépense commune au foyer
    label        VARCHAR(140),
    recorded_by  UUID         NOT NULL,
    recorded_at  TIMESTAMPTZ  NOT NULL
);
CREATE INDEX expense_household_month ON expense (household_id, spent_on);
