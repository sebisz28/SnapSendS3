package com.snapsends3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.snapsends3.entity.CleanupJob;
import com.snapsends3.entity.Share;
import com.snapsends3.entity.ShareFile;
import com.snapsends3.repository.CleanupJobRepository;
import com.snapsends3.repository.DownloadAttemptRepository;
import com.snapsends3.repository.ShareFileRepository;
import com.snapsends3.repository.ShareRepository;
import com.snapsends3.storage.ObjectStorage;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ShareCleanupWorkerTest {
    private final Instant now = Instant.parse("2026-10-22T16:00:00Z");
    private final UUID shareId = UUID.randomUUID();
    private final ShareRepository shares = mock(ShareRepository.class);
    private final ShareFileRepository files = mock(ShareFileRepository.class);
    private final DownloadAttemptRepository attempts = mock(DownloadAttemptRepository.class);
    private final CleanupJobRepository jobs = mock(CleanupJobRepository.class);
    private final ObjectStorage storage = mock(ObjectStorage.class);
    private final ShareCleanupWorker worker = new ShareCleanupWorker(shares, files, attempts, jobs, storage,
            Clock.fixed(now, ZoneOffset.UTC));

    @Test
    void deletesExpiredObjectsThenMetadataAndJob() {
        Share share = mock(Share.class);
        ShareFile file = mock(ShareFile.class);
        CleanupJob job = new CleanupJob(shareId, now);
        when(shares.findTop100ByExpiresAtLessThanEqual(now)).thenReturn(List.of());
        when(jobs.findTop100ByAttemptCountLessThanOrderByCreatedAtAsc(3)).thenReturn(List.of(job));
        when(shares.findById(shareId)).thenReturn(Optional.of(share));
        when(share.getId()).thenReturn(shareId);
        when(share.getArchiveObjectKey()).thenReturn("shares/archive.zip");
        when(files.findByShareId(shareId)).thenReturn(List.of(file));
        when(file.getObjectKey()).thenReturn("shares/original.pdf");

        worker.cleanExpiredShares();

        verify(storage).delete("shares/original.pdf");
        verify(storage).delete("shares/archive.zip");
        verify(attempts).deleteByShareId(shareId);
        verify(files).deleteByShareId(shareId);
        verify(shares).delete(share);
        verify(jobs).delete(job);
    }

    @Test
    void retainsJobForRetryWhenObjectDeletionFails() {
        Share share = mock(Share.class);
        ShareFile file = mock(ShareFile.class);
        CleanupJob job = new CleanupJob(shareId, now);
        when(shares.findTop100ByExpiresAtLessThanEqual(now)).thenReturn(List.of());
        when(jobs.findTop100ByAttemptCountLessThanOrderByCreatedAtAsc(3)).thenReturn(List.of(job));
        when(shares.findById(shareId)).thenReturn(Optional.of(share));
        when(share.getId()).thenReturn(shareId);
        when(files.findByShareId(shareId)).thenReturn(List.of(file));
        when(file.getObjectKey()).thenReturn("shares/original.pdf");
        doThrow(new IllegalStateException("R2 unavailable")).when(storage).delete("shares/original.pdf");

        worker.cleanExpiredShares();

        assertThat(job.getAttemptCount()).isEqualTo(1);
        verifyNoInteractions(attempts);
        verify(files).findByShareId(shareId);
    }
}
