package com.snapsends3.repository;

import com.snapsends3.entity.ShareFile;
import com.snapsends3.entity.ShareFileScanStatus;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShareFileRepository extends JpaRepository<ShareFile, UUID> {
    boolean existsByShareId(UUID shareId);

    java.util.List<ShareFile> findByShareId(UUID shareId);

    long countByShareId(UUID shareId);

    long countByShareIdAndScanStatus(UUID shareId, ShareFileScanStatus scanStatus);

    @Query("select coalesce(sum(file.sizeBytes), 0) from ShareFile file where file.share.id = :shareId")
    long totalSizeByShareId(UUID shareId);

    @Query("select coalesce(sum(file.sizeBytes), 0) from ShareFile file")
    long totalSize();

    long deleteByShareId(UUID shareId);
}
