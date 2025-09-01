package dev.xpepelok.flowly.configuration.credentials;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("service.database.tables")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DatabaseTableProperties {
    String transactionTableName;
    String legalUserDataTableName;
    String userDataTableName;
}
