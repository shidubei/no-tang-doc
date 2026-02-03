package com.ntdoc.notangdoccore.config.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="digitalocean.spaces")
public class SpacesProperties {
    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;

    private String bucket;
    private int presignMinutes = 15;
    private String keyPrefix = "ntdoc";
}
