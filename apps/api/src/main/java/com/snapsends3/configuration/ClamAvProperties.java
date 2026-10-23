package com.snapsends3.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "snapsends3.clamav")
public class ClamAvProperties {
    private String host;
    private int port;
    private int timeoutSeconds;
}
