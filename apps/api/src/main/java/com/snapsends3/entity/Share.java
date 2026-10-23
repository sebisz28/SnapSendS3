package com.snapsends3.entity;

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
public class Share {
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

    @Column(name = "password_hash", length = 255, updatable = false)
    private String passwordHash;

    public Share(String token, String senderSubject, Instant expiresAt) {
        this(token, senderSubject, expiresAt, null);
    }

    public Share(String token, String senderSubject, Instant expiresAt, String passwordHash) {
        this.token = token;
        this.senderSubject = senderSubject;
        this.expiresAt = expiresAt;
        this.passwordHash = passwordHash;
        this.status = ShareStatus.UPLOADING;
    }

    public boolean acceptsUploads(Instant now) {
        return status == ShareStatus.UPLOADING && expiresAt.isAfter(now);
    }

    public void finalizeUploads() {
        if (status != ShareStatus.UPLOADING) {
            throw new IllegalStateException("Only uploading Shares can be finalized");
        }
        this.status = ShareStatus.SCANNING;
    }

    public void markReady(String archiveObjectKey) {
        if (status != ShareStatus.SCANNING) {
            throw new IllegalStateException("Only scanning Shares can become ready");
        }
        this.archiveObjectKey = archiveObjectKey;
        this.status = ShareStatus.READY;
    }

    public void reject() {
        this.status = ShareStatus.REJECTED;
    }

    public void expire() {
        this.status = ShareStatus.EXPIRED;
    }
}
