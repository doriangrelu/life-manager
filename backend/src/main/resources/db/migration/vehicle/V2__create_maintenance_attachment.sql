-- Pièces jointes du carnet d'entretien (factures, photos). Le contenu est sur le disque du serveur,
-- sous storage_key ; la base ne garde que les métadonnées.
CREATE TABLE maintenance_attachment
(
    id          UUID PRIMARY KEY,
    entry_id    UUID         NOT NULL REFERENCES maintenance_entry (id) ON DELETE CASCADE,
    file_name   VARCHAR(120) NOT NULL,
    media_type  VARCHAR(10)  NOT NULL, -- PDF, JPEG, PNG ou WEBP
    size_bytes  BIGINT       NOT NULL CHECK (size_bytes BETWEEN 1 AND 10485760),
    storage_key UUID         NOT NULL UNIQUE,
    added_by    UUID         NOT NULL,
    added_at    TIMESTAMPTZ  NOT NULL
);
CREATE INDEX maintenance_attachment_entry ON maintenance_attachment (entry_id);
