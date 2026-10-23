package com.snapsends3.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfiguration {
    @Bean
    OpenAPI snapSendS3OpenApi() {
        return new OpenAPI().info(new Info().title("SnapSendS3 API").version("v1")
                .description("Secure temporary file sharing API."));
    }
}
