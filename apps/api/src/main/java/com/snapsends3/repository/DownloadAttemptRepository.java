package com.snapsends3.repository;

import com.snapsends3.entity.DownloadAttempt;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DownloadAttemptRepository extends JpaRepository<DownloadAttempt, UUID> {
    long deleteByShareId(UUID shareId);
}
