package com.snapsends3.repository;

import com.snapsends3.entity.Share;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareRepository extends JpaRepository<Share, UUID> {
    Optional<Share> findByToken(String token);

    java.util.List<Share> findTop100ByExpiresAtLessThanEqual(java.time.Instant expiresAt);
}
