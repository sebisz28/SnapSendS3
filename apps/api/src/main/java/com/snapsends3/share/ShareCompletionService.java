package com.snapsends3.share;

import com.snapsends3.storage.ObjectStorage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ShareCompletionService {
    private final ShareRepository shares;
    private final ShareFileRepository files;
    private final ObjectStorage storage;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeIfPossible(UUID shareId) {
        Share share = shares.findById(shareId).orElseThrow();
        List<ShareFile> shareFiles = files.findByShareId(shareId);
        if (shareFiles.stream().anyMatch(file -> file.getScanStatus() == ShareFileScanStatus.INFECTED)) {
            share.reject();
            return;
        }
        if (shareFiles.stream().anyMatch(file -> file.getScanStatus() != ShareFileScanStatus.CLEAN)) {
            return;
        }
        if (shareFiles.size() == 1) {
            share.markReady(shareFiles.getFirst().getObjectKey());
            return;
        }
        String archiveKey = "shares/" + shareId + "/download.zip";
        createArchive(shareFiles, archiveKey);
        share.markReady(archiveKey);
    }

    private void createArchive(List<ShareFile> files, String archiveKey) {
        Path archive = null;
        try {
            archive = Files.createTempFile("snapsends3-", ".zip");
            try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(archive))) {
                for (int index = 0; index < files.size(); index++) {
                    ShareFile file = files.get(index);
                    output.putNextEntry(new ZipEntry(String.format("%03d-%s", index + 1, safeName(file.getOriginalName()))));
                    try (InputStream input = storage.openForScan(file.getObjectKey())) {
                        input.transferTo(output);
                    }
                    output.closeEntry();
                }
            }
            storage.putArchive(archiveKey, archive);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create share archive", exception);
        } finally {
            if (archive != null) {
                try {
                    Files.deleteIfExists(archive);
                } catch (IOException ignored) {
                    // Best effort cleanup of temporary archive storage.
                }
            }
        }
    }

    private String safeName(String originalName) {
        return Path.of(originalName).getFileName().toString();
    }
}
