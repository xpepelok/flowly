package dev.xpepelok.flowly.configuration;

import dev.xpepelok.flowly.configuration.credentials.GrpcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcConfiguration {
}
