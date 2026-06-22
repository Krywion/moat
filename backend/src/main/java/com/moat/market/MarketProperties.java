package com.moat.market;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.market")
public record MarketProperties(String baseUrl, String defaultSuffix, int timeoutMs, boolean enabled) {
}
