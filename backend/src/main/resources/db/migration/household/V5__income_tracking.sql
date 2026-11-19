-- Réglage du foyer : suivi des rentrées (bilan), désactivé par défaut. Rien ne change pour les foyers existants.
ALTER TABLE household ADD COLUMN income_tracking BOOLEAN NOT NULL DEFAULT FALSE;
