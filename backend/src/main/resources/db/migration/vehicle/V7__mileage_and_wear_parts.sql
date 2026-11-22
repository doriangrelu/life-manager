-- Kilométrage actuel et taille des pneus du véhicule (facultatifs).
ALTER TABLE vehicle
    ADD COLUMN current_mileage_km INTEGER CHECK (current_mileage_km >= 0),
    ADD COLUMN tire_size          VARCHAR(30);

-- Pièces d'usure : montée tant que removed_on est vide ; une seule pièce montée par type et par véhicule.
CREATE TABLE wear_part
(
    id               UUID PRIMARY KEY,
    vehicle_id       UUID        NOT NULL REFERENCES vehicle (id) ON DELETE CASCADE,
    kind             VARCHAR(20) NOT NULL,
    label            VARCHAR(60),
    fitted_on        DATE        NOT NULL,
    fitted_km        INTEGER     NOT NULL CHECK (fitted_km >= 0),
    replace_after_km INTEGER CHECK (replace_after_km > 0),
    removed_on       DATE,
    removed_km       INTEGER CHECK (removed_km >= 0)
);
CREATE UNIQUE INDEX wear_part_mounted ON wear_part (vehicle_id, kind) WHERE removed_on IS NULL;
CREATE INDEX wear_part_vehicle ON wear_part (vehicle_id);
