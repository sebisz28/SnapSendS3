package com.snapsends3.share;

import com.snapsends3.openapi.api.SharesApi;
import com.snapsends3.openapi.model.CreateUploadIntentRequest;
import com.snapsends3.openapi.model.ShareResponse;
import com.snapsends3.openapi.model.UploadIntentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class ShareController implements SharesApi {
    private final ShareService shares;

    @Override
    public ResponseEntity<ShareResponse> createShare(String senderSubject) {
        return ResponseEntity.status(201).body(toResponse(shares.create(senderSubject)));
    }

    @Override
    public ResponseEntity<UploadIntentResponse> createUploadIntent(UUID shareId, CreateUploadIntentRequest request) {
        ShareService.UploadIntent intent = shares.createUploadIntent(
                shareId, request.getFileName(), request.getSizeBytes(), request.getContentType());
        return ResponseEntity.status(201).body(new UploadIntentResponse()
                .fileId(intent.file().getId())
                .objectKey(intent.file().getObjectKey())
                .uploadUrl(intent.uploadUrl()));
    }

    @Override
    public ResponseEntity<ShareResponse> finalizeShare(UUID shareId) {
        return ResponseEntity.ok(toResponse(shares.finalizeShare(shareId)));
    }

    private ShareResponse toResponse(Share share) {
        return new ShareResponse()
                .id(share.getId())
                .token(share.getToken())
                .expiresAt(share.getExpiresAt().atOffset(java.time.ZoneOffset.UTC))
                .status(ShareResponse.StatusEnum.valueOf(share.getStatus().name()));
    }
}
