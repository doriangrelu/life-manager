-- Les rappels suivent aussi les charges récurrentes, dont le nom peut atteindre 60 caractères.
ALTER TABLE deadline ALTER COLUMN subject TYPE VARCHAR(60);
