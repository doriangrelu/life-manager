-- Propriétaire de chaque foyer. Reprise : le plus ancien membre (le créateur, pour tous les foyers existants).
ALTER TABLE household ADD COLUMN owner_id UUID;
UPDATE household h
SET owner_id = (SELECT m.member_id FROM membership m WHERE m.household_id = h.id ORDER BY m.joined_at, m.id LIMIT 1);
ALTER TABLE household ALTER COLUMN owner_id SET NOT NULL;
