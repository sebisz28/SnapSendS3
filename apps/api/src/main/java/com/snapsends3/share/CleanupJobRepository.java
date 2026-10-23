package com.snapsends3.share;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface CleanupJobRepository extends JpaRepository<CleanupJob, UUID> {
    boolean existsByShareId(UUID shareId);

    List<CleanupJob> findTop100ByAttemptCountLessThanOrderByCreatedAtAsc(int attemptCount);
}
