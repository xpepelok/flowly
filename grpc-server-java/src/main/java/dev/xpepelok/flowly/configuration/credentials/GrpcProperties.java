package dev.xpepelok.flowly.configuration.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("service")
public record GrpcProperties(int port) {

}
