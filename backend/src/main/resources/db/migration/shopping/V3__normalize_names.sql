-- Noms enregistrés sans espaces superflus et en minuscules (même règle que Names.normalized).
-- Sur les colonnes uniques, une ligne n'est convertie que si sa forme normalisée n'est pas déjà prise.

UPDATE shopping_item
SET label = lower(regexp_replace(btrim(label), '\s+', ' ', 'g'));

UPDATE shopping_list AS l
SET name = lower(regexp_replace(btrim(l.name), '\s+', ' ', 'g'))
WHERE l.name <> lower(regexp_replace(btrim(l.name), '\s+', ' ', 'g'))
  AND NOT EXISTS (SELECT 1
                  FROM shopping_list AS other
                  WHERE other.household_id = l.household_id
                    AND other.id <> l.id
                    AND lower(other.name) = lower(regexp_replace(btrim(l.name), '\s+', ' ', 'g')));

UPDATE purchase_history AS h
SET label = lower(regexp_replace(btrim(h.label), '\s+', ' ', 'g'))
WHERE h.label <> lower(regexp_replace(btrim(h.label), '\s+', ' ', 'g'))
  AND NOT EXISTS (SELECT 1
                  FROM purchase_history AS other
                  WHERE other.list_id = h.list_id
                    AND other.id <> h.id
                    AND lower(other.label) = lower(regexp_replace(btrim(h.label), '\s+', ' ', 'g')));
