package com.hashicorp.vaulttransit;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "transit")
public class VaultTransitProperties {
    private String path;
    private String key;

    @Value("${transit.event.data.key}")
    private String eventsDataKeyPath;

    @Value("${transit.event.key}")
    private String eventsKey;

}