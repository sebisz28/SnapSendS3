package com.snapsends3.share;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface ShareRepository extends JpaRepository<Share, UUID> {
    Optional<Share> findByToken(String token);
}
