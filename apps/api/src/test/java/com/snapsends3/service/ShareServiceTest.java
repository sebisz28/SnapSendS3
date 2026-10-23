package com.snapsends3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.snapsends3.configuration.ShareLimitProperties;
import com.snapsends3.entity.Share;
import com.snapsends3.entity.ShareFile;
import com.snapsends3.entity.ShareStatus;
import com.snapsends3.repository.DownloadAttemptRepository;
import com.snapsends3.repository.ShareFileRepository;
import com.snapsends3.repository.ShareRepository;
import com.snapsends3.storage.ObjectStorage;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ShareServiceTest {
    @Test
    void createsUploadingShareThatExpiresInSixHours() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-22T10:00:00Z"), ZoneOffset.UTC);
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        ShareFileRepository files = Mockito.mock(ShareFileRepository.class);
        ObjectStorage storage = Mockito.mock(ObjectStorage.class);
        ShareScanWorker scanWorker = Mockito.mock(ShareScanWorker.class);
        DownloadAttemptRepository attempts = Mockito.mock(DownloadAttemptRepository.class);
        ShareLimitProperties limits = new ShareLimitProperties();
        limits.setFileBytes(104857600);
        limits.setShareBytes(524288000);
        when(repository.save(any(Share.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ShareService service = new ShareService(repository, files, storage, scanWorker, clock, limits, attempts);

        Share share = service.create("sender-123");

        assertThat(share.getSenderSubject()).isEqualTo("sender-123");
        assertThat(share.getStatus()).isEqualTo(ShareStatus.UPLOADING);
        assertThat(share.getExpiresAt()).isEqualTo(Instant.parse("2026-10-22T16:00:00Z"));
        assertThat(share.getToken()).isNotBlank();
    }

    @Test
    void finalizesAShareWithFiles() {
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        ShareFileRepository files = Mockito.mock(ShareFileRepository.class);
        ObjectStorage storage = Mockito.mock(ObjectStorage.class);
        ShareScanWorker scanWorker = Mockito.mock(ShareScanWorker.class);
        DownloadAttemptRepository attempts = Mockito.mock(DownloadAttemptRepository.class);
        ShareLimitProperties limits = new ShareLimitProperties();
        limits.setFileBytes(100);
        limits.setShareBytes(500);
        UUID shareId = UUID.randomUUID();
        Share share = new Share("token", "sender", Instant.parse("2026-10-22T00:00:00Z"));

        when(repository.findById(shareId)).thenReturn(Optional.of(share));
        when(files.existsByShareId(shareId)).thenReturn(true);
        when(repository.save(any(Share.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Clock clock = Clock.fixed(Instant.parse("2026-10-21T23:59:59Z"), ZoneOffset.UTC);
        when(files.findByShareId(shareId)).thenReturn(java.util.List.of());
        Share finalized = new ShareService(repository, files, storage, scanWorker, clock, limits, attempts).finalizeShare(shareId, "sender");

        assertThat(finalized.getStatus()).isEqualTo(ShareStatus.SCANNING);
    }

    @Test
    void rejectsUploadsAfterExpiry() {
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        ShareFileRepository files = Mockito.mock(ShareFileRepository.class);
        ObjectStorage storage = Mockito.mock(ObjectStorage.class);
        ShareScanWorker scanWorker = Mockito.mock(ShareScanWorker.class);
        DownloadAttemptRepository attempts = Mockito.mock(DownloadAttemptRepository.class);
        ShareLimitProperties limits = new ShareLimitProperties();
        limits.setFileBytes(100);
        limits.setShareBytes(500);
        UUID shareId = UUID.randomUUID();
        Share expired = new Share("token", "sender", Instant.parse("2026-10-22T00:00:00Z"));
        Clock clock = Clock.fixed(Instant.parse("2026-10-22T00:00:01Z"), ZoneOffset.UTC);

        when(repository.findById(shareId)).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> new ShareService(repository, files, storage, scanWorker, clock, limits, attempts)
                .createUploadIntent(shareId, "sender", "packing-list.pdf", 10, "application/pdf"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share no longer accepts uploads");
    }

    @Test
    void namesSingleFileDownloadsAfterTheOriginalFile() {
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        ShareFileRepository files = Mockito.mock(ShareFileRepository.class);
        ObjectStorage storage = Mockito.mock(ObjectStorage.class);
        DownloadAttemptRepository attempts = Mockito.mock(DownloadAttemptRepository.class);
        Share share = new Share("token", "sender", Instant.parse("2026-10-22T00:00:00Z"));
        share.finalizeUploads();
        share.markReady("shares/share-id/object-id");
        when(repository.findByToken("token")).thenReturn(Optional.of(share));
        when(files.findByShareId(share.getId())).thenReturn(java.util.List.of(new ShareFile(share, "photo.png", 10, "shares/share-id/object-id")));
        when(storage.createDownloadUrl(any(), any())).thenReturn(URI.create("https://storage.example/download"));

        new ShareService(repository, files, storage, Mockito.mock(ShareScanWorker.class),
                Clock.fixed(Instant.parse("2026-10-21T00:00:00Z"), ZoneOffset.UTC), limits(Long.MAX_VALUE), attempts)
                .createPublicDownload("token");

        verify(storage).createDownloadUrl("shares/share-id/object-id", "photo.png");
    }

    @Test
    void namesMultiFileDownloadsAsZipArchive() {
        // Given: a ready share with two uploaded files
        // When: a recipient requests its public download URL
        // Then: object storage receives the ZIP archive name
        fail("Test skeleton - not implemented");
    }

    @Test
    void preservesPublicDownloadAuthorizationResponses() {
        // Given: unready, expired, and password-protected shares
        // When: a recipient requests a public download URL
        // Then: the existing 409, 410, and 401 results are returned
        fail("Test skeleton - not implemented");
    }

    @Test
    void returnsStatusToTheShareSender() {
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        UUID shareId = UUID.randomUUID();
        Share share = new Share("token", "sender-123", Instant.parse("2026-10-22T00:00:00Z"));
        when(repository.findById(shareId)).thenReturn(Optional.of(share));

        Share result = service(repository).getForSender(shareId, "sender-123");

        assertThat(result).isSameAs(share);
    }

    @Test
    void rejectsStatusRequestsFromAnotherSender() {
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        UUID shareId = UUID.randomUUID();
        when(repository.findById(shareId)).thenReturn(Optional.of(
                new Share("token", "sender-123", Instant.parse("2026-10-22T00:00:00Z"))));

        assertThatThrownBy(() -> service(repository).getForSender(shareId, "sender-456"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(error -> ((ResponseStatusException) error).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void reportsAnExpiredShareAsExpired() {
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        UUID shareId = UUID.randomUUID();
        Share share = new Share("token", "sender-123", Instant.parse("2026-10-22T00:00:00Z"));
        when(repository.findById(shareId)).thenReturn(Optional.of(share));
        when(repository.save(share)).thenReturn(share);

        Share result = service(repository, Clock.fixed(Instant.parse("2026-10-23T00:00:01Z"), ZoneOffset.UTC))
                .getForSender(shareId, "sender-123");

        assertThat(result.getStatus()).isEqualTo(ShareStatus.EXPIRED);
    }

    @Test
    void rejectsNewSharesWhenTheConfiguredStorageBudgetIsReached() {
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        ShareFileRepository files = Mockito.mock(ShareFileRepository.class);
        ShareLimitProperties limits = limits(1_000);
        when(files.totalSize()).thenReturn(1_000L);

        assertThatThrownBy(() -> service(repository, files, limits).create("sender-123"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(error -> ((ResponseStatusException) error).getStatusCode())
                .isEqualTo(HttpStatus.INSUFFICIENT_STORAGE);
    }

    @Test
    void rejectsUploadsThatWouldReachTheConfiguredStorageBudget() {
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        ShareFileRepository files = Mockito.mock(ShareFileRepository.class);
        ShareLimitProperties limits = limits(1_000);
        UUID shareId = UUID.randomUUID();
        when(repository.findById(shareId)).thenReturn(Optional.of(
                new Share("token", "sender-123", Instant.parse("2026-10-22T00:00:00Z"))));
        when(files.totalSizeByShareId(shareId)).thenReturn(0L);
        when(files.totalSize()).thenReturn(900L);

        assertThatThrownBy(() -> service(repository, files, limits)
                .createUploadIntent(shareId, "sender-123", "packing-list.pdf", 100, "application/pdf"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(error -> ((ResponseStatusException) error).getStatusCode())
                .isEqualTo(HttpStatus.INSUFFICIENT_STORAGE);
    }

    private ShareService service(ShareRepository repository) {
        return service(repository, Clock.fixed(Instant.parse("2026-10-21T00:00:00Z"), ZoneOffset.UTC));
    }

    private ShareService service(ShareRepository repository, Clock clock) {
        return service(repository, Mockito.mock(ShareFileRepository.class), limits(Long.MAX_VALUE), clock);
    }

    private ShareService service(ShareRepository repository, ShareFileRepository files, ShareLimitProperties limits) {
        return service(repository, files, limits, Clock.fixed(Instant.parse("2026-10-21T00:00:00Z"), ZoneOffset.UTC));
    }

    private ShareService service(ShareRepository repository, ShareFileRepository files, ShareLimitProperties limits, Clock clock) {
        return new ShareService(repository, files, Mockito.mock(ObjectStorage.class),
                Mockito.mock(ShareScanWorker.class), clock, limits, Mockito.mock(DownloadAttemptRepository.class));
    }

    private ShareLimitProperties limits(long storageBytes) {
        ShareLimitProperties limits = new ShareLimitProperties();
        limits.setFileBytes(100);
        limits.setShareBytes(500);
        limits.setStorageBytes(storageBytes);
        return limits;
    }
}
