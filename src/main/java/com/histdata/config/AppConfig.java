package com.histdata.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig {

    @Bean
    @Profile("aws")
    S3Client s3Client(AppProperties properties) {
        return S3Client.builder()
                .region(Region.of(properties.awsRegion()))
                .build();
    }
}

