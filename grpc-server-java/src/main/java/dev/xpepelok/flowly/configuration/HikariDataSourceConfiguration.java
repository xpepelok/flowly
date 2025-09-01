package dev.xpepelok.flowly.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.xpepelok.flowly.configuration.credentials.DatabaseAuthProperties;
import dev.xpepelok.flowly.configuration.credentials.DatabaseTableProperties;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({DatabaseAuthProperties.class, DatabaseTableProperties.class})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HikariDataSourceConfiguration {
    @Bean
    public HikariDataSource hikariDataSource(DatabaseAuthProperties databaseAuthProperties) {
        var hikariConfig = new HikariConfig();

        var url = "jdbc:mysql://%s:%d/%s".formatted(
                databaseAuthProperties.getHost(),
                databaseAuthProperties.getPort(),
                databaseAuthProperties.getName()
        );
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(databaseAuthProperties.getUsername());

        var databasePassword = databaseAuthProperties.getPassword();
        if (databasePassword != null && !databasePassword.isBlank()) {
            hikariConfig.setPassword(databaseAuthProperties.getPassword());
        }

        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        return new HikariDataSource(hikariConfig);
    }
}
