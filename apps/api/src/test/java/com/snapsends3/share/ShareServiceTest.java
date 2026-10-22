package com.snapsends3.share;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.snapsends3.config.ShareLimitProperties;
import com.snapsends3.storage.ObjectStorage;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

class ShareServiceTest {
    @Test
    void createsUploadingShareThatExpiresInSixHours() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-22T10:00:00Z"), ZoneOffset.UTC);
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        ShareFileRepository files = Mockito.mock(ShareFileRepository.class);
        ObjectStorage storage = Mockito.mock(ObjectStorage.class);
        ShareScanWorker scanWorker = Mockito.mock(ShareScanWorker.class);
        ShareLimitProperties limits = new ShareLimitProperties();
        limits.setFileBytes(104857600);
        limits.setShareBytes(524288000);
        when(repository.save(any(Share.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ShareService service = new ShareService(repository, files, storage, scanWorker, clock, limits);

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
        Share finalized = new ShareService(repository, files, storage, scanWorker, clock, limits).finalizeShare(shareId);

        assertThat(finalized.getStatus()).isEqualTo(ShareStatus.SCANNING);
    }

    @Test
    void rejectsUploadsAfterExpiry() {
        ShareRepository repository = Mockito.mock(ShareRepository.class);
        ShareFileRepository files = Mockito.mock(ShareFileRepository.class);
        ObjectStorage storage = Mockito.mock(ObjectStorage.class);
        ShareScanWorker scanWorker = Mockito.mock(ShareScanWorker.class);
        ShareLimitProperties limits = new ShareLimitProperties();
        limits.setFileBytes(100);
        limits.setShareBytes(500);
        UUID shareId = UUID.randomUUID();
        Share expired = new Share("token", "sender", Instant.parse("2026-10-22T00:00:00Z"));
        Clock clock = Clock.fixed(Instant.parse("2026-10-22T00:00:01Z"), ZoneOffset.UTC);

        when(repository.findById(shareId)).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> new ShareService(repository, files, storage, scanWorker, clock, limits)
                .createUploadIntent(shareId, "packing-list.pdf", 10, "application/pdf"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Share no longer accepts uploads");
    }
}
