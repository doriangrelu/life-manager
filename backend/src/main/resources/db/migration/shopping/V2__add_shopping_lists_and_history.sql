-- Sous-listes de courses (« Courses », « Bricolage »…) et historique des achats.

CREATE TABLE shopping_list
(
    id           UUID PRIMARY KEY,
    household_id UUID        NOT NULL,
    name         VARCHAR(30) NOT NULL
);
-- Un nom n'apparaît qu'une fois par foyer, sans tenir compte des majuscules.
CREATE UNIQUE INDEX shopping_list_name_once ON shopping_list (household_id, lower(name));

-- Reprise : chaque foyer déjà connu reçoit les trois sous-listes par défaut.
INSERT INTO shopping_list (id, household_id, name)
SELECT gen_random_uuid(), known.household_id, defaults.name
FROM (SELECT household_id FROM household_member
      UNION
      SELECT household_id FROM shopping_item) AS known
         CROSS JOIN (VALUES ('Courses'), ('Bricolage'), ('Vêtements')) AS defaults (name);

-- Les articles existants rejoignent la sous-liste « Courses » de leur foyer.
ALTER TABLE shopping_item ADD COLUMN list_id UUID REFERENCES shopping_list (id) ON DELETE CASCADE;
UPDATE shopping_item item
SET list_id = list.id
FROM shopping_list list
WHERE list.household_id = item.household_id
  AND list.name = 'Courses';
ALTER TABLE shopping_item ALTER COLUMN list_id SET NOT NULL;
-- Le foyer se retrouve désormais via la sous-liste.
ALTER TABLE shopping_item DROP COLUMN household_id;
CREATE INDEX shopping_item_list ON shopping_item (list_id);

-- Une ligne par article déjà acheté dans une sous-liste.
CREATE TABLE purchase_history
(
    id                UUID PRIMARY KEY,
    list_id           UUID        NOT NULL REFERENCES shopping_list (id) ON DELETE CASCADE,
    label             VARCHAR(60) NOT NULL,
    last_purchased_at TIMESTAMPTZ NOT NULL,
    purchase_count    INTEGER     NOT NULL CHECK (purchase_count >= 1)
);
CREATE UNIQUE INDEX purchase_history_label_once ON purchase_history (list_id, lower(label));
