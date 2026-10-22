CREATE TABLE shares (
    id UUID PRIMARY KEY,
    token VARCHAR(128) NOT NULL UNIQUE,
    sender_subject VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(32) NOT NULL,
    CONSTRAINT shares_status_check CHECK (status IN ('UPLOADING', 'SCANNING', 'ARCHIVING', 'READY', 'REJECTED', 'EXPIRED'))
);

CREATE INDEX shares_expiry_idx ON shares (expires_at);
