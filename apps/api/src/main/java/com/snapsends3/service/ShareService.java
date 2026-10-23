package com.snapsends3.service;

import com.snapsends3.configuration.ShareLimitProperties;
import com.snapsends3.entity.DownloadAttempt;
import com.snapsends3.entity.DownloadAttemptOutcome;
import com.snapsends3.entity.Share;
import com.snapsends3.entity.ShareFile;
import com.snapsends3.entity.ShareStatus;
import com.snapsends3.repository.DownloadAttemptRepository;
import com.snapsends3.repository.ShareFileRepository;
import com.snapsends3.repository.ShareRepository;
import com.snapsends3.storage.ObjectStorage;
import java.net.URI;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ShareService {
    private final ShareRepository shares;
    private final ShareFileRepository shareFiles;
    private final ObjectStorage objectStorage;
    private final ShareScanWorker scanWorker;
    private final Clock clock;
    private final ShareLimitProperties limits;
    private final DownloadAttemptRepository downloadAttempts;

    public Share create(String senderSubject) {
        return create(senderSubject, null);
    }

    public Share create(String senderSubject, String password) {
        if (password != null && (password.length() < 8 || password.length() > 128)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be between 8 and 128 characters");
        }
        if (shareFiles.totalSize() >= limits.getStorageBytes()) {
            throw new ResponseStatusException(HttpStatus.INSUFFICIENT_STORAGE, "Configured demo storage budget has been reached");
        }
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return shares.save(new Share(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes), senderSubject,
                clock.instant().plus(Duration.ofHours(6)), password == null ? null : SharePasswordHasher.hash(password)));
    }

    public Share getForSender(UUID shareId, String senderSubject) {
        Share share = findShare(shareId);
        if (!share.getSenderSubject().equals(senderSubject)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Share does not belong to the sender");
        }
        if (!share.getExpiresAt().isAfter(clock.instant()) && share.getStatus() != ShareStatus.EXPIRED) {
            share.expire();
            shares.save(share);
        }
        return share;
    }

    public UploadIntent createUploadIntent(UUID shareId, String senderSubject, String fileName, long sizeBytes, String contentType) {
        if (fileName == null || fileName.isBlank() || sizeBytes <= 0 || sizeBytes > limits.getFileBytes()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file upload request");
        }
        Share share = findForSender(shareId, senderSubject);
        if (!share.acceptsUploads(clock.instant())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Share no longer accepts uploads");
        }
        if (shareFiles.totalSizeByShareId(shareId) > limits.getShareBytes() - sizeBytes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Share exceeds configured upload limit");
        }
        if (shareFiles.totalSize() >= limits.getStorageBytes() - sizeBytes) {
            throw new ResponseStatusException(HttpStatus.INSUFFICIENT_STORAGE, "Configured demo storage budget would be exceeded");
        }
        String objectKey = "shares/" + shareId + "/" + UUID.randomUUID();
        ShareFile file = shareFiles.save(new ShareFile(share, fileName, sizeBytes, objectKey));
        return new UploadIntent(file, objectStorage.createUploadUrl(objectKey, contentType));
    }

    public Share finalizeShare(UUID shareId, String senderSubject) {
        Share share = findForSender(shareId, senderSubject);
        if (!share.acceptsUploads(clock.instant())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Share no longer accepts uploads");
        }
        if (!shareFiles.existsByShareId(shareId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A share needs at least one file");
        }
        share.finalizeUploads();
        Share finalizedShare = shares.save(share);
        shareFiles.findByShareId(shareId).forEach(file -> scanWorker.scanAsync(file.getId()));
        return finalizedShare;
    }

    public PublicDownload createPublicDownload(String token) {
        return createPublicDownload(token, null);
    }

    public PublicDownload createPublicDownload(String token, String password) {
        Share share = shares.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Share not found"));
        var now = clock.instant();
        if (!share.getExpiresAt().isAfter(now)) {
            recordDownloadAttempt(share, DownloadAttemptOutcome.EXPIRED, now);
            throw new ResponseStatusException(HttpStatus.GONE, "Share has expired");
        }
        if (share.getStatus() != ShareStatus.READY) {
            recordDownloadAttempt(share, DownloadAttemptOutcome.NOT_READY, now);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Share is not ready for download");
        }
        if (share.getPasswordHash() != null && !SharePasswordHasher.matches(share.getPasswordHash(), password)) {
            recordDownloadAttempt(share, DownloadAttemptOutcome.PASSWORD_FAILED, now);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password required or incorrect");
        }
        var files = shareFiles.findByShareId(share.getId());
        String downloadFileName = files.size() == 1 ? files.getFirst().getOriginalName() : "snapsends3-share.zip";
        var downloadUrl = objectStorage.createDownloadUrl(share.getArchiveObjectKey(), downloadFileName);
        recordDownloadAttempt(share, DownloadAttemptOutcome.ALLOWED, now);
        return new PublicDownload(downloadUrl, share.getExpiresAt());
    }

    private void recordDownloadAttempt(Share share, DownloadAttemptOutcome outcome, java.time.Instant occurredAt) {
        downloadAttempts.save(new DownloadAttempt(share.getId(), outcome, occurredAt));
    }

    public String storageWarning() {
        long usage = shareFiles.totalSize();
        long warningAt = limits.getStorageBytes() / 100 * limits.getStorageWarningPercent();
        return usage >= warningAt
                ? "Configured demo storage budget is at or above " + limits.getStorageWarningPercent() + "% usage."
                : null;
    }

    private Share findShare(UUID shareId) {
        return shares.findById(shareId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Share not found"));
    }

    private Share findForSender(UUID shareId, String senderSubject) {
        return getForSender(shareId, senderSubject);
    }

    public record UploadIntent(ShareFile file, URI uploadUrl) {
    }

    public record PublicDownload(URI downloadUrl, java.time.Instant expiresAt) {
    }
}
