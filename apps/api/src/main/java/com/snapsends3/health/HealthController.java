package com.snapsends3.health;

import com.snapsends3.openapi.api.SystemApi;
import com.snapsends3.openapi.model.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HealthController implements SystemApi {
    @Override
    public ResponseEntity<HealthResponse> getHealth() {
        return ResponseEntity.ok(new HealthResponse()
                .application("SnapSendS3")
                .status("UP"));
    }
}
