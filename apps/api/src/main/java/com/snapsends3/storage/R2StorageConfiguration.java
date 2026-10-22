package com.snapsends3.storage;

import com.snapsends3.config.R2StorageProperties;
import java.net.URI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
class R2StorageConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "snapsends3.storage", name = {"endpoint", "bucket", "access-key-id", "secret-access-key"})
    ObjectStorage r2ObjectStorage(R2StorageProperties properties) {
        StaticCredentialsProvider credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(
                properties.getAccessKeyId(), properties.getSecretAccessKey()));
        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();
        S3Presigner presigner = S3Presigner.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of("auto"))
                .serviceConfiguration(serviceConfiguration)
                .credentialsProvider(credentials)
                .build();
        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of("auto"))
                .serviceConfiguration(serviceConfiguration)
                .credentialsProvider(credentials)
                .build();
        return new R2ObjectStorage(presigner, client, properties);
    }

    @Bean
    @ConditionalOnMissingBean(ObjectStorage.class)
    ObjectStorage unavailableObjectStorage() {
        return new ObjectStorage() {
            @Override
            public URI createUploadUrl(String objectKey, String contentType) {
                throw unavailable();
            }

            @Override
            public java.io.InputStream openForScan(String objectKey) {
                throw unavailable();
            }

            @Override
            public URI createDownloadUrl(String objectKey) {
                throw unavailable();
            }

            @Override
            public void putArchive(String objectKey, java.nio.file.Path archive) {
                throw unavailable();
            }

            private ResponseStatusException unavailable() {
                return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Object storage is not configured");
            }
        };
    }
}
