-- Tables du domaine véhicule (schéma vehicle). Aucune référence vers un autre schéma.
-- Coûts en centimes d'euro (BIGINT), jamais de nombre à virgule.

-- Copie locale des membres de chaque foyer, alimentée par les événements du domaine foyer.
CREATE TABLE household_member
(
    id           UUID PRIMARY KEY,
    household_id UUID NOT NULL,
    member_id    UUID NOT NULL,
    CONSTRAINT household_member_once UNIQUE (household_id, member_id)
);

CREATE TABLE vehicle
(
    id                       UUID PRIMARY KEY,
    household_id             UUID        NOT NULL,
    name                     VARCHAR(40) NOT NULL,
    license_plate            VARCHAR(12),
    technical_inspection_due DATE, -- NULL = pas d'échéance connue
    service_due              DATE
);
CREATE INDEX vehicle_household ON vehicle (household_id);

-- Le carnet d'un véhicule disparaît avec lui.
CREATE TABLE maintenance_entry
(
    id           UUID PRIMARY KEY,
    vehicle_id   UUID        NOT NULL REFERENCES vehicle (id) ON DELETE CASCADE,
    performed_on DATE        NOT NULL,
    kind         VARCHAR(30) NOT NULL,
    mileage_km   INTEGER CHECK (mileage_km >= 0),
    cost_cents   BIGINT CHECK (cost_cents >= 0),
    note         VARCHAR(140),
    recorded_by  UUID        NOT NULL,
    recorded_at  TIMESTAMPTZ NOT NULL
);
CREATE INDEX maintenance_entry_vehicle ON maintenance_entry (vehicle_id, performed_on);
