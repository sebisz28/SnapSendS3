package com.snapsends3.repository;

import com.snapsends3.entity.CleanupJob;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleanupJobRepository extends JpaRepository<CleanupJob, UUID> {
    boolean existsByShareId(UUID shareId);

    List<CleanupJob> findTop100ByAttemptCountLessThanOrderByCreatedAtAsc(int attemptCount);
}
