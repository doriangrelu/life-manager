-- Ordre choisi par le foyer : position des articles dans leur sous-liste et des sous-listes dans le foyer.
-- Valeurs de départ = ordre affiché jusqu'ici (articles par date d'ajout, sous-listes par nom).

ALTER TABLE shopping_item ADD COLUMN position INTEGER;
UPDATE shopping_item item
SET position = ranked.rank
FROM (SELECT id, ROW_NUMBER() OVER (PARTITION BY list_id ORDER BY added_at, id) - 1 AS rank
      FROM shopping_item) AS ranked
WHERE ranked.id = item.id;
ALTER TABLE shopping_item ALTER COLUMN position SET NOT NULL;

ALTER TABLE shopping_list ADD COLUMN position INTEGER;
UPDATE shopping_list list
SET position = ranked.rank
FROM (SELECT id, ROW_NUMBER() OVER (PARTITION BY household_id ORDER BY lower(name), id) - 1 AS rank
      FROM shopping_list) AS ranked
WHERE ranked.id = list.id;
ALTER TABLE shopping_list ALTER COLUMN position SET NOT NULL;
