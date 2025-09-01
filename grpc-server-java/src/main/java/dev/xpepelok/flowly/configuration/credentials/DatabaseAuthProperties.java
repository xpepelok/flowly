package dev.xpepelok.flowly.configuration.credentials;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties("service.database.auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseAuthProperties {
    String host;
    int port;
    String name;
    String username;
    String password;
}
