-- Tables du domaine courses (schéma shopping). Aucune référence vers un autre schéma.

-- Copie locale des membres de chaque foyer, alimentée par les événements du domaine foyer.
CREATE TABLE household_member
(
    id           UUID PRIMARY KEY,
    household_id UUID NOT NULL,
    member_id    UUID NOT NULL,
    CONSTRAINT household_member_once UNIQUE (household_id, member_id)
);

CREATE TABLE shopping_item
(
    id           UUID PRIMARY KEY,
    household_id UUID        NOT NULL,
    label        VARCHAR(60) NOT NULL,
    checked_at   TIMESTAMPTZ, -- NULL = pas encore coché
    added_by     UUID        NOT NULL,
    added_at     TIMESTAMPTZ NOT NULL
);
CREATE INDEX shopping_item_household ON shopping_item (household_id);
