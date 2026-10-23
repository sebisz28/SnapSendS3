package com.snapsends3.service;

import com.snapsends3.entity.CleanupJob;
import com.snapsends3.repository.CleanupJobRepository;
import com.snapsends3.repository.DownloadAttemptRepository;
import com.snapsends3.repository.ShareFileRepository;
import com.snapsends3.repository.ShareRepository;
import com.snapsends3.storage.ObjectStorage;
import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShareCleanupWorker {
    private static final int MAX_ATTEMPTS = 3;

    private final ShareRepository shares;
    private final ShareFileRepository files;
    private final DownloadAttemptRepository downloadAttempts;
    private final CleanupJobRepository cleanupJobs;
    private final ObjectStorage storage;
    private final Clock clock;

    @Scheduled(fixedDelayString = "PT1M")
    @Transactional
    public void cleanExpiredShares() {
        Instant now = clock.instant();
        shares.findTop100ByExpiresAtLessThanEqual(now).forEach(share -> {
            if (!cleanupJobs.existsByShareId(share.getId())) {
                cleanupJobs.save(new CleanupJob(share.getId(), now));
            }
        });
        cleanupJobs.findTop100ByAttemptCountLessThanOrderByCreatedAtAsc(MAX_ATTEMPTS)
                .forEach(job -> clean(job, now));
    }

    private void clean(CleanupJob job, Instant now) {
        job.recordAttempt(now);
        shares.findById(job.getShareId()).ifPresentOrElse(share -> {
            try {
                HashSet<String> objectKeys = new HashSet<>();
                files.findByShareId(share.getId()).forEach(file -> objectKeys.add(file.getObjectKey()));
                if (share.getArchiveObjectKey() != null) objectKeys.add(share.getArchiveObjectKey());
                objectKeys.forEach(storage::delete);
                downloadAttempts.deleteByShareId(share.getId());
                files.deleteByShareId(share.getId());
                shares.delete(share);
                cleanupJobs.delete(job);
            } catch (RuntimeException ignored) {
                // A persisted attempt count makes cleanup retryable without recording object keys or error details.
            }
        }, () -> cleanupJobs.delete(job));
    }
}
