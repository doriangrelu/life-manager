-- Tables du domaine des charges récurrentes (schéma bills). Aucune référence vers un autre schéma.
-- Montants en centimes d'euro (BIGINT), jamais de nombre à virgule.

-- Copie locale des membres de chaque foyer, alimentée par les événements du domaine foyer.
CREATE TABLE household_member
(
    id           UUID PRIMARY KEY,
    household_id UUID NOT NULL,
    member_id    UUID NOT NULL,
    CONSTRAINT household_member_once UNIQUE (household_id, member_id)
);

CREATE TABLE recurring_charge
(
    id                 UUID PRIMARY KEY,
    household_id       UUID        NOT NULL,
    name               VARCHAR(60) NOT NULL,
    amount_cents       BIGINT      NOT NULL CHECK (amount_cents BETWEEN 0 AND 10000000),
    amount_kind        VARCHAR(10) NOT NULL,
    frequency          VARCHAR(10) NOT NULL,
    first_due_on       DATE        NOT NULL,
    budget_category_id UUID        NOT NULL, -- catégorie du domaine budget (sans clé étrangère)
    payer_id           UUID        NOT NULL,
    beneficiary_id     UUID,                 -- NULL = tout le foyer
    active             BOOLEAN     NOT NULL
);
CREATE INDEX recurring_charge_household ON recurring_charge (household_id);

-- Une échéance par charge et par date : le calendrier ne peut jamais en créer deux.
CREATE TABLE charge_occurrence
(
    id             UUID PRIMARY KEY,
    charge_id      UUID        NOT NULL REFERENCES recurring_charge (id),
    household_id   UUID        NOT NULL,
    due_on         DATE        NOT NULL,
    expected_cents BIGINT      NOT NULL CHECK (expected_cents >= 0),
    status         VARCHAR(10) NOT NULL,
    paid_cents     BIGINT CHECK (paid_cents >= 0),
    paid_on        DATE,
    CONSTRAINT occurrence_once_per_date UNIQUE (charge_id, due_on)
);
CREATE INDEX charge_occurrence_open ON charge_occurrence (household_id, status);

CREATE TABLE invoice
(
    id            UUID PRIMARY KEY,
    occurrence_id UUID         NOT NULL REFERENCES charge_occurrence (id),
    file_name     VARCHAR(120) NOT NULL,
    media_type    VARCHAR(10)  NOT NULL,
    size_bytes    BIGINT       NOT NULL,
    storage_key   UUID         NOT NULL UNIQUE,
    added_by      UUID         NOT NULL,
    added_at      TIMESTAMPTZ  NOT NULL
);
CREATE INDEX invoice_occurrence ON invoice (occurrence_id);
