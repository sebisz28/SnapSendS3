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
import lombok.*;

@Entity
@Table(name = "shares")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Share {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String token;

    @Column(nullable = false, updatable = false)
    private String senderSubject;

    @Column(nullable = false, updatable = false)
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareStatus status;

    @Column
    private String archiveObjectKey;

    Share(String token, String senderSubject, Instant expiresAt) {
        this.token = token;
        this.senderSubject = senderSubject;
        this.expiresAt = expiresAt;
        this.status = ShareStatus.UPLOADING;
    }

    boolean acceptsUploads(Instant now) {
        return status == ShareStatus.UPLOADING && expiresAt.isAfter(now);
    }

    void finalizeUploads() {
        if (status != ShareStatus.UPLOADING) {
            throw new IllegalStateException("Only uploading Shares can be finalized");
        }
        this.status = ShareStatus.SCANNING;
    }

    void markReady(String archiveObjectKey) {
        if (status != ShareStatus.SCANNING) {
            throw new IllegalStateException("Only scanning Shares can become ready");
        }
        this.archiveObjectKey = archiveObjectKey;
        this.status = ShareStatus.READY;
    }

    void reject() {
        this.status = ShareStatus.REJECTED;
    }
}
