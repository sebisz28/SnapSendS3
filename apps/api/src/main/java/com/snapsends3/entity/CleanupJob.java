package com.snapsends3.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cleanup_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CleanupJob {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "share_id", nullable = false, unique = true, updatable = false)
    private UUID shareId;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    public CleanupJob(UUID shareId, Instant createdAt) {
        this.shareId = shareId;
        this.createdAt = createdAt;
    }

    public void recordAttempt(Instant attemptedAt) {
        this.attemptCount++;
        this.lastAttemptAt = attemptedAt;
    }
}
