package com.snapsends3.share;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DownloadAttemptRepository extends JpaRepository<DownloadAttempt, UUID> {
    long deleteByShareId(UUID shareId);
}
