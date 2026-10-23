package com.snapsends3.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class R2ObjectStorageTest {
    @Test
    void signsAnAttachmentDispositionWithASanitizedFilename() {
        // Given: an object key and filename containing quotes and line breaks
        // When: a signed download URL is created
        // Then: the signed request has a safe attachment disposition
        fail("Test skeleton - not implemented");
    }
}
