-- Noms de foyer enregistrés sans espaces superflus et en minuscules (même règle que Names.normalized).
UPDATE household
SET name = lower(regexp_replace(btrim(name), '\s+', ' ', 'g'));
