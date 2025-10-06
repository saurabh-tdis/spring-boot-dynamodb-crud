package com.app.dynamodb.shared;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.dynamodb")
public class AwsConfigProperties {
    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;
    private Map<String, String> tables;
}