package com.snapsends3.storage;

import static org.assertj.core.api.Assertions.assertThat;

import com.snapsends3.configuration.R2StorageProperties;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

class R2ObjectStorageTest {
    @Test
    void signsAnAttachmentDispositionWithASanitizedFilename() {
        R2StorageProperties properties = new R2StorageProperties();
        properties.setBucket("snapsends3");
        try (S3Presigner presigner = S3Presigner.builder()
                .endpointOverride(URI.create("https://storage.example"))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("access", "secret")))
                .build()) {
            R2ObjectStorage storage = new R2ObjectStorage(presigner, null, properties);

            URI downloadUrl = storage.createDownloadUrl("shares/share-id/file", "report\"\r\n2026.pdf");

            assertThat(URLDecoder.decode(downloadUrl.getRawQuery(), StandardCharsets.UTF_8))
                    .contains("response-content-disposition=attachment; filename=\"report___2026.pdf\"");
        }
    }
}
