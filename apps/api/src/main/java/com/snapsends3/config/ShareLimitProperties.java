package com.snapsends3.config;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Positive
    private long storageBytes = Long.MAX_VALUE;

    @Min(1)
    @Max(100)
    private int storageWarningPercent = 80;
}
