package com.snapsends3.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "snapsends3.storage")
public class R2StorageProperties {
    private String endpoint;
    private String bucket;
    private String accessKeyId;
    private String secretAccessKey;
}
