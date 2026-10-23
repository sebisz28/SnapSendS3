package com.snapsends3.storage;

import com.snapsends3.configuration.R2StorageProperties;
import java.net.URI;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
public class R2ObjectStorage implements ObjectStorage {
    private static final Duration UPLOAD_URL_TTL = Duration.ofMinutes(10);

    private final S3Presigner presigner;
    private final S3Client client;
    private final R2StorageProperties properties;

    @Override
    public URI createUploadUrl(String objectKey, String contentType) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(objectKey)
                .contentType(contentType)
                .build();
        PresignedPutObjectRequest request = presigner.presignPutObject(PutObjectPresignRequest.builder()
                .signatureDuration(UPLOAD_URL_TTL)
                .putObjectRequest(putRequest)
                .build());
        return URI.create(request.url().toString());
    }

    @Override
    public java.io.InputStream openForScan(String objectKey) {
        return client.getObject(GetObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(objectKey)
                .build());
    }

    @Override
    public URI createDownloadUrl(String objectKey, String downloadFileName) {
        String safeFileName = downloadFileName.replaceAll("[\\r\\n\\\"]", "_");
        PresignedGetObjectRequest request = presigner.presignGetObject(GetObjectPresignRequest.builder()
                .signatureDuration(UPLOAD_URL_TTL)
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(properties.getBucket())
                        .key(objectKey)
                        .responseContentDisposition("attachment; filename=\"" + safeFileName + "\"")
                        .build())
                .build());
        return URI.create(request.url().toString());
    }

    @Override
    public void putArchive(String objectKey, java.nio.file.Path archive) {
        client.putObject(PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(objectKey)
                .contentType("application/zip")
                .build(), RequestBody.fromFile(archive));
    }

    @Override
    public void delete(String objectKey) {
        client.deleteObject(DeleteObjectRequest.builder().bucket(properties.getBucket()).key(objectKey).build());
    }
}
