CREATE TABLE share_files (
    id UUID PRIMARY KEY,
    share_id UUID NOT NULL REFERENCES shares (id) ON DELETE CASCADE,
    original_name VARCHAR(512) NOT NULL,
    size_bytes BIGINT NOT NULL CHECK (size_bytes > 0),
    object_key VARCHAR(512) NOT NULL UNIQUE,
    scan_status VARCHAR(32) NOT NULL,
    CONSTRAINT share_files_scan_status_check CHECK (scan_status IN ('PENDING', 'CLEAN', 'INFECTED', 'FAILED'))
);

CREATE INDEX share_files_share_id_idx ON share_files (share_id);
