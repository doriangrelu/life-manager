-- Historique des rappels saisis marqués « c'est fait » (lot 6c).
-- title est recopié : la ligne reste si le rappel est supprimé ensuite. Purgé au-delà d'un an.
CREATE TABLE reminder_done
(
    id           UUID PRIMARY KEY,
    household_id UUID        NOT NULL,
    title        VARCHAR(60) NOT NULL,
    due_on       DATE        NOT NULL,
    done_at      TIMESTAMPTZ NOT NULL,
    done_by      UUID        NOT NULL
);
CREATE INDEX reminder_done_household ON reminder_done (household_id, done_at DESC);
CREATE INDEX reminder_done_done_at ON reminder_done (done_at);
