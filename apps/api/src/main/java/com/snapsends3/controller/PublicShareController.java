package com.snapsends3.controller;

import com.snapsends3.dto.CreatePublicDownloadRequest;
import com.snapsends3.dto.PublicDownloadResponse;
import com.snapsends3.openapi.api.PublicSharesApi;
import com.snapsends3.service.ShareService;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicShareController implements PublicSharesApi {
    private final ShareService shares;

    @Override
    public ResponseEntity<PublicDownloadResponse> createPublicDownload(String token, CreatePublicDownloadRequest request) {
        ShareService.PublicDownload download = shares.createPublicDownload(token, request.getPassword());
        return ResponseEntity.ok(new PublicDownloadResponse()
                .downloadUrl(download.downloadUrl())
                .expiresAt(download.expiresAt().atOffset(ZoneOffset.UTC)));
    }
}
