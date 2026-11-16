-- Tables du domaine foyer (schéma household). Aucune référence vers un autre schéma.

CREATE TABLE household
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL
);

-- member_id = sub Keycloak de l'utilisateur (pas de clé étrangère : il vit dans un autre domaine).
CREATE TABLE membership
(
    id           UUID PRIMARY KEY,
    household_id UUID        NOT NULL REFERENCES household (id),
    member_id    UUID        NOT NULL,
    joined_at    TIMESTAMPTZ NOT NULL,
    CONSTRAINT membership_once_per_household UNIQUE (household_id, member_id)
);
CREATE INDEX membership_member_idx ON membership (member_id);

CREATE TABLE invitation
(
    id           UUID PRIMARY KEY,
    household_id UUID        NOT NULL REFERENCES household (id),
    code         CHAR(8)     NOT NULL UNIQUE,
    status       VARCHAR(10) NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED', 'REVOKED')),
    expires_at   TIMESTAMPTZ NOT NULL,
    accepted_by  UUID
);
-- Au plus un code en attente par foyer.
CREATE UNIQUE INDEX invitation_one_pending_per_household ON invitation (household_id) WHERE status = 'PENDING';

-- Copie locale des noms d'affichage, alimentée par les événements du domaine identité.
CREATE TABLE member_profile
(
    member_id    UUID PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    updated_at   TIMESTAMPTZ  NOT NULL
);
