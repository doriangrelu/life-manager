-- Tables du domaine rappels (schéma reminder). Aucune référence vers un autre schéma.

-- Copie locale des membres de chaque foyer, alimentée par les événements du domaine foyer.
CREATE TABLE household_member
(
    id           UUID PRIMARY KEY,
    household_id UUID NOT NULL,
    member_id    UUID NOT NULL,
    CONSTRAINT household_member_once UNIQUE (household_id, member_id)
);

-- Copie des échéances à surveiller, alimentée par les événements des autres domaines (véhicules).
CREATE TABLE deadline
(
    id           UUID PRIMARY KEY,
    household_id UUID        NOT NULL,
    source_id    UUID        NOT NULL, -- objet d'origine (ex. le véhicule)
    kind         VARCHAR(30) NOT NULL,
    subject      VARCHAR(40) NOT NULL,
    due_on       DATE        NOT NULL,
    CONSTRAINT deadline_once_per_source UNIQUE (source_id, kind)
);
CREATE INDEX deadline_household ON deadline (household_id);
