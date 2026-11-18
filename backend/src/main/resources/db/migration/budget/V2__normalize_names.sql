-- Noms enregistrés sans espaces superflus et en minuscules (la PWA remet la majuscule à l'affichage).
-- Même règle que Names.normalized côté Java.

-- Catégories : nom unique par foyer. Une catégorie n'est convertie que si sa forme normalisée
-- n'est pas déjà prise : la migration ne peut pas échouer sur un doublon.
UPDATE category AS c
SET name = lower(regexp_replace(btrim(c.name), '\s+', ' ', 'g'))
WHERE c.name <> lower(regexp_replace(btrim(c.name), '\s+', ' ', 'g'))
  AND NOT EXISTS (SELECT 1
                  FROM category AS other
                  WHERE other.household_id = c.household_id
                    AND other.id <> c.id
                    AND lower(other.name) = lower(regexp_replace(btrim(c.name), '\s+', ' ', 'g')));

UPDATE expense
SET label = lower(regexp_replace(btrim(label), '\s+', ' ', 'g'))
WHERE label IS NOT NULL;
