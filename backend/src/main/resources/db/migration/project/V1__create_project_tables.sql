-- Tables du domaine projets (schéma project). Aucune référence vers un autre schéma.

-- Copie locale des membres de chaque foyer (alimentée par les événements du domaine foyer).
CREATE TABLE household_member
(
    id           UUID PRIMARY KEY,
    household_id UUID NOT NULL,
    member_id    UUID NOT NULL,
    CONSTRAINT household_member_once UNIQUE (household_id, member_id)
);

CREATE TABLE project
(
    id               UUID PRIMARY KEY,
    household_id     UUID        NOT NULL,
    name             VARCHAR(60) NOT NULL,
    description      VARCHAR(500),
    color            VARCHAR(20) NOT NULL,
    planned_cents    BIGINT CHECK (planned_cents >= 0),
    start_on         DATE,
    target_on        DATE,
    status           VARCHAR(20) NOT NULL,
    counts_in_budget BOOLEAN     NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL
);
CREATE INDEX project_household ON project (household_id);

CREATE TABLE project_task
(
    id           UUID PRIMARY KEY,
    project_id   UUID         NOT NULL REFERENCES project (id),
    household_id UUID         NOT NULL,
    title        VARCHAR(120) NOT NULL,
    assignee_id  UUID,
    due_on       DATE,
    done_at      TIMESTAMPTZ,
    position     INTEGER      NOT NULL
);
CREATE INDEX project_task_project ON project_task (project_id, position);
CREATE INDEX project_task_household ON project_task (household_id);

-- Boîte d'envoi : événements de contrat enregistrés avec le changement, distribués ensuite.
CREATE TABLE outbox
(
    id         BIGSERIAL PRIMARY KEY,
    type       VARCHAR(200) NOT NULL,
    payload    TEXT         NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    sent_at    TIMESTAMPTZ,
    attempts   INTEGER      NOT NULL DEFAULT 0,
    last_error VARCHAR(1000)
);
CREATE INDEX outbox_pending ON outbox (id) WHERE sent_at IS NULL;

-- Auteur de chaque objet (export des données personnelles).
CREATE TABLE authorship
(
    object_id    UUID PRIMARY KEY,
    household_id UUID NOT NULL,
    member_id    UUID NOT NULL
);
CREATE INDEX authorship_member ON authorship (member_id);
CREATE INDEX authorship_household ON authorship (household_id);
