CREATE TABLE download_attempts (
    id UUID PRIMARY KEY,
    share_id UUID NOT NULL REFERENCES shares(id),
    outcome VARCHAR(32) NOT NULL CHECK (outcome IN ('ALLOWED', 'PASSWORD_FAILED', 'NOT_READY', 'EXPIRED')),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX download_attempts_share_created_idx ON download_attempts (share_id, created_at DESC);
