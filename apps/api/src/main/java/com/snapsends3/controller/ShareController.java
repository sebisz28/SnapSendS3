package com.snapsends3.controller;

import com.snapsends3.dto.CreateShareRequest;
import com.snapsends3.dto.CreateUploadIntentRequest;
import com.snapsends3.dto.ShareResponse;
import com.snapsends3.dto.UploadIntentResponse;
import com.snapsends3.entity.Share;
import com.snapsends3.openapi.api.SharesApi;
import com.snapsends3.security.SenderIdentity;
import com.snapsends3.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShareController implements SharesApi {
    private final ShareService shares;
    private final SenderIdentity sender;

    @Override
    public ResponseEntity<ShareResponse> createShare(CreateShareRequest request) {
        return ResponseEntity.status(201).body(toResponse(shares.create(sender.subject(), request.getPassword())));
    }

    @Override
    public ResponseEntity<ShareResponse> getShare(UUID shareId) {
        return ResponseEntity.ok(toResponse(shares.getForSender(shareId, sender.subject())));
    }

    @Override
    public ResponseEntity<UploadIntentResponse> createUploadIntent(UUID shareId, CreateUploadIntentRequest request) {
        ShareService.UploadIntent intent = shares.createUploadIntent(
                shareId, sender.subject(), request.getFileName(), request.getSizeBytes(), request.getContentType());
        return ResponseEntity.status(201).body(new UploadIntentResponse()
                .fileId(intent.file().getId())
                .objectKey(intent.file().getObjectKey())
                .uploadUrl(intent.uploadUrl()));
    }

    @Override
    public ResponseEntity<ShareResponse> finalizeShare(UUID shareId) {
        return ResponseEntity.ok(toResponse(shares.finalizeShare(shareId, sender.subject())));
    }

    private ShareResponse toResponse(Share share) {
        return new ShareResponse()
                .id(share.getId())
                .token(share.getToken())
                .expiresAt(share.getExpiresAt().atOffset(java.time.ZoneOffset.UTC))
                .status(ShareResponse.StatusEnum.valueOf(share.getStatus().name()))
                .storageWarning(shares.storageWarning());
    }
}
