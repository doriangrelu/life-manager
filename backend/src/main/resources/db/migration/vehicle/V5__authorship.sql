-- Auteur des objets créés à partir de la v0.13.0 (export des données personnelles).
CREATE TABLE authorship
(
    object_id    UUID PRIMARY KEY,
    household_id UUID NOT NULL,
    member_id    UUID NOT NULL
);
CREATE INDEX authorship_member ON authorship (member_id);
CREATE INDEX authorship_household ON authorship (household_id);
