-- Profil local des utilisateurs (schéma identity).
-- id = sub Keycloak : le même identifiant est utilisé dans tous les domaines.
CREATE TABLE user_account
(
    id            UUID PRIMARY KEY,
    email         VARCHAR(254) NOT NULL,
    display_name  VARCHAR(100) NOT NULL,
    registered_at TIMESTAMPTZ  NOT NULL
);
