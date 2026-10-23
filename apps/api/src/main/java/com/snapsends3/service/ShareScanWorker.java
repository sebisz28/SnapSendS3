package com.snapsends3.service;

import com.snapsends3.entity.ShareFile;
import com.snapsends3.repository.ShareFileRepository;
import com.snapsends3.scan.MalwareScanner;
import com.snapsends3.storage.ObjectStorage;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class ShareScanWorker {
    private static final int MAX_ATTEMPTS = 3;

    private final ShareFileRepository files;
    private final ObjectStorage storage;
    private final MalwareScanner scanner;
    private final ShareCompletionService completion;

    @Async
    @Transactional
    public void scanAsync(UUID fileId) {
        ShareFile file = files.findById(fileId).orElseThrow();
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try (InputStream input = storage.openForScan(file.getObjectKey())) {
                if (scanner.scan(input) == MalwareScanner.ScanResult.INFECTED) {
                    file.markInfected();
                } else {
                    file.markClean();
                }
                completeAfterCommit(file.getShare().getId());
                return;
            } catch (IOException | RuntimeException exception) {
                if (attempt == MAX_ATTEMPTS) {
                    file.markFailed();
                    completeAfterCommit(file.getShare().getId());
                }
            }
        }
    }

    private void completeAfterCommit(UUID shareId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                completion.completeIfPossible(shareId);
            }
        });
    }
}
