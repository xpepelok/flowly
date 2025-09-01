package dev.xpepelok.flowly.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractTable implements CloseableTable {
    HikariDataSource hikariDataSource;
    @Getter
    String tableName;

    protected void forceQuery(String query) {
        try (Connection cn = hikariDataSource.getConnection(); Statement st = cn.createStatement()) {
            st.executeUpdate(query);
        } catch (SQLException e) {
            throw new IllegalArgumentException(String.format("Unable to create database table with query: %s", query));
        }
    }

    @Override
    public void close() {
        this.hikariDataSource.close();
    }
}