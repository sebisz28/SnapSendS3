package com.snapsends3.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "share_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShareFile {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "share_id", nullable = false)
    private Share share;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false, unique = true)
    private String objectKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareFileScanStatus scanStatus;

    public ShareFile(Share share, String originalName, long sizeBytes, String objectKey) {
        this.share = share;
        this.originalName = originalName;
        this.sizeBytes = sizeBytes;
        this.objectKey = objectKey;
        this.scanStatus = ShareFileScanStatus.PENDING;
    }

    public void markClean() {
        this.scanStatus = ShareFileScanStatus.CLEAN;
    }

    public void markInfected() {
        this.scanStatus = ShareFileScanStatus.INFECTED;
    }

    public void markFailed() {
        this.scanStatus = ShareFileScanStatus.FAILED;
    }
}
