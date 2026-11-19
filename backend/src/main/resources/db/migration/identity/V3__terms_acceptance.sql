-- Conditions d'utilisation acceptées (lot 6c) : version (une date) et moment ; null tant que rien n'est accepté.
ALTER TABLE user_account ADD COLUMN terms_version VARCHAR(20);
ALTER TABLE user_account ADD COLUMN terms_accepted_at TIMESTAMPTZ;
