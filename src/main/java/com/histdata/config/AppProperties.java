package com.histdata.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "histdata")
public record AppProperties(String localRoot, String s3Bucket, String awsRegion) {
}

