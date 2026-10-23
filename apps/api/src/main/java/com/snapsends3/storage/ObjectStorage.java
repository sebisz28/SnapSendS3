package com.snapsends3.storage;

import java.net.URI;
import java.io.InputStream;
import java.nio.file.Path;

public interface ObjectStorage {
    URI createUploadUrl(String objectKey, String contentType);

    InputStream openForScan(String objectKey);

    URI createDownloadUrl(String objectKey);

    void putArchive(String objectKey, Path archive);

    void delete(String objectKey);
}
