package com.snapsends3.config;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "snapsends3.limits")
public class ShareLimitProperties {
    @Positive
    private long fileBytes;

    @Positive
    private long shareBytes;
}
