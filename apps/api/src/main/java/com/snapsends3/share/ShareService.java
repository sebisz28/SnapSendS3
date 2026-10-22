package com.snapsends3.share;

import com.snapsends3.config.ShareLimitProperties;
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
class ShareService {
    private final ShareRepository shares;
    private final ShareFileRepository shareFiles;
    private final ObjectStorage objectStorage;
    private final ShareScanWorker scanWorker;
    private final Clock clock;
    private final ShareLimitProperties limits;

    Share create(String senderSubject) {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return shares.save(new Share(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes), senderSubject,
                clock.instant().plus(Duration.ofHours(6))));
    }

    UploadIntent createUploadIntent(UUID shareId, String fileName, long sizeBytes, String contentType) {
        if (fileName == null || fileName.isBlank() || sizeBytes <= 0 || sizeBytes > limits.getFileBytes()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file upload request");
        }
        Share share = findShare(shareId);
        if (!share.acceptsUploads(clock.instant())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Share no longer accepts uploads");
        }
        if (shareFiles.totalSizeByShareId(shareId) > limits.getShareBytes() - sizeBytes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Share exceeds configured upload limit");
        }
        String objectKey = "shares/" + shareId + "/" + UUID.randomUUID();
        ShareFile file = shareFiles.save(new ShareFile(share, fileName, sizeBytes, objectKey));
        return new UploadIntent(file, objectStorage.createUploadUrl(objectKey, contentType));
    }

    Share finalizeShare(UUID shareId) {
        Share share = findShare(shareId);
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

    PublicDownload createPublicDownload(String token) {
        Share share = shares.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Share not found"));
        if (!share.getExpiresAt().isAfter(clock.instant())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Share has expired");
        }
        if (share.getStatus() != ShareStatus.READY) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Share is not ready for download");
        }
        return new PublicDownload(objectStorage.createDownloadUrl(share.getArchiveObjectKey()), share.getExpiresAt());
    }

    private Share findShare(UUID shareId) {
        return shares.findById(shareId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Share not found"));
    }

    record UploadIntent(ShareFile file, URI uploadUrl) {
    }

    record PublicDownload(URI downloadUrl, java.time.Instant expiresAt) {
    }
}
