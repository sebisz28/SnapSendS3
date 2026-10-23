CREATE TABLE cleanup_jobs (
    id UUID PRIMARY KEY,
    share_id UUID NOT NULL UNIQUE,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    last_attempt_at TIMESTAMPTZ
);

CREATE INDEX cleanup_jobs_pending_idx ON cleanup_jobs (attempt_count, created_at);
