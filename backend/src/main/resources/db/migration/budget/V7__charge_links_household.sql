-- Liens échéance → dépense / rentrée : ils portent désormais leur foyer, pour partir avec lui
-- même quand la dépense ou la rentrée a été supprimée par un membre (le lien reste alors seul).

ALTER TABLE charge_expense ADD COLUMN household_id UUID;
UPDATE charge_expense AS link SET household_id = e.household_id FROM expense AS e WHERE e.id = link.expense_id;
CREATE INDEX charge_expense_household ON charge_expense (household_id);

ALTER TABLE charge_income ADD COLUMN household_id UUID;
UPDATE charge_income AS link SET household_id = i.household_id FROM income AS i WHERE i.id = link.income_id;
CREATE INDEX charge_income_household ON charge_income (household_id);

-- Les anciens liens dont la dépense ou la rentrée a déjà disparu gardent un foyer vide (introuvable) :
-- ce ne sont que des identifiants d'échéance, sans aucune donnée personnelle.
