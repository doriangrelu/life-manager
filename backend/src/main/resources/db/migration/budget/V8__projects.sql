-- Lot 8 : dépense rattachée à un projet (sans clé étrangère : le projet vit dans le domaine project).
ALTER TABLE expense ADD COLUMN project_id UUID;
CREATE INDEX expense_project ON expense (project_id) WHERE project_id IS NOT NULL;

-- Copie locale des projets, tenue par les événements du domaine project.
CREATE TABLE project_ref
(
    project_id       UUID PRIMARY KEY,
    household_id     UUID        NOT NULL,
    name             VARCHAR(60) NOT NULL,
    status           VARCHAR(20) NOT NULL,
    counts_in_budget BOOLEAN     NOT NULL
);
CREATE INDEX project_ref_household ON project_ref (household_id);

-- Dépenses qui comptent dans le budget : toutes, sauf celles d'un projet réglé « hors budget ».
CREATE VIEW counted_expense AS
SELECT e.*
FROM expense e
WHERE NOT EXISTS (SELECT 1 FROM project_ref p WHERE p.project_id = e.project_id AND NOT p.counts_in_budget);
