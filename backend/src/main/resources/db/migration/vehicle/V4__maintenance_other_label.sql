-- Précision facultative d'une opération de type « Autre » (« Essuie-glaces »).
ALTER TABLE maintenance_entry
    ADD COLUMN other_label VARCHAR(40);
