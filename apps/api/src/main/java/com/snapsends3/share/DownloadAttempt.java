package com.snapsends3.share;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "download_attempts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class DownloadAttempt {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "share_id", nullable = false, updatable = false)
    private UUID shareId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32, updatable = false)
    private DownloadAttemptOutcome outcome;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    DownloadAttempt(UUID shareId, DownloadAttemptOutcome outcome, Instant createdAt) {
        this.shareId = shareId;
        this.outcome = outcome;
        this.createdAt = createdAt;
    }
}
