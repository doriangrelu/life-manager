-- Sens d'une charge récurrente (lot 4a) : EXPENSE (toutes les charges existantes) ou INCOME (rentrée récurrente).
ALTER TABLE recurring_charge ADD COLUMN direction VARCHAR(10) NOT NULL DEFAULT 'EXPENSE'
    CHECK (direction IN ('EXPENSE', 'INCOME'));
