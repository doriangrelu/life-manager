-- Boîte d'envoi : événements de contrat enregistrés avec le changement, distribués ensuite.
CREATE TABLE outbox (
    id         BIGSERIAL PRIMARY KEY,
    type       VARCHAR(200)  NOT NULL,
    payload    TEXT          NOT NULL,
    created_at TIMESTAMPTZ   NOT NULL,
    sent_at    TIMESTAMPTZ,
    attempts   INTEGER       NOT NULL DEFAULT 0,
    last_error VARCHAR(1000)
);

-- Le facteur ne lit que les événements non distribués.
CREATE INDEX outbox_pending ON outbox (id) WHERE sent_at IS NULL;
