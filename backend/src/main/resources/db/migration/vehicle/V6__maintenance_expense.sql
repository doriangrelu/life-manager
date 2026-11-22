-- Opération du carnet envoyée au budget : catégorie choisie et payeur.
-- Une ligne au plus par opération ; elle disparaît avec l'opération.
CREATE TABLE maintenance_expense
(
    entry_id     UUID PRIMARY KEY REFERENCES maintenance_entry (id) ON DELETE CASCADE,
    household_id UUID NOT NULL,
    category_id  UUID NOT NULL, -- catégorie du budget (autre domaine, sans clé étrangère)
    payer_id     UUID NOT NULL
);
