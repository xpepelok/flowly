package dev.xpepelok.flowly.database.user;

import com.google.protobuf.ByteString;
import com.zaxxer.hikari.HikariDataSource;
import dev.xpepelok.flowly.configuration.credentials.DatabaseTableProperties;
import dev.xpepelok.flowly.data.model.BankUser;
import dev.xpepelok.flowly.data.model.PersonalOwnerData;
import dev.xpepelok.flowly.database.AbstractTable;
import dev.xpepelok.flowly.util.SerializationUtil;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserDataTableImpl extends AbstractTable implements UserDataTable {

    public UserDataTableImpl(HikariDataSource hikariDataSource, DatabaseTableProperties props) {
        super(hikariDataSource, props.getUserDataTableName());

        this.forceQuery(String.format(
                "CREATE TABLE IF NOT EXISTS `%s` ("
                        + "`registration_id` BINARY(16) PRIMARY KEY, "
                        + "`first_name` VARCHAR(16), "
                        + "`middle_name` VARCHAR(16), "
                        + "`last_name` VARCHAR(16), "
                        + "`born_date` BIGINT, "
                        + "`address` VARCHAR(128), "
                        + "`iban` VARCHAR(34), "
                        + "INDEX `idx_last_name` (`last_name`), "
                        + "INDEX `idx_iban` (`iban`)"
                        + ");",
                tableName
        ));
    }

    @Override
    public Optional<BankUser> getUser(byte[] uuid) {
        String sql = String.format("SELECT * FROM `%s` WHERE `registration_id` = ?;", tableName);
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setBytes(1, uuid);
            var rs = st.executeQuery();
            if (!rs.next()) return Optional.empty();
            return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    String.format("Unable to get user data for user %s", SerializationUtil.getUUID(uuid)), e);
        }
    }

    @Override
    public Optional<BankUser> getUser(String iban) {
        String sql = String.format("SELECT * FROM `%s` WHERE `iban` = ?;", tableName);
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, iban);
            var rs = st.executeQuery();
            if (!rs.next()) return Optional.empty();
            return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new IllegalArgumentException(String.format("Unable to get user by IBAN %s", iban), e);
        }
    }

    @Override
    public List<BankUser> getUsers(int offset, int limit) {
        String sql = String.format(
                "SELECT * FROM `%s` ORDER BY `registration_id` LIMIT ? OFFSET ?;",
                tableName
        );
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setInt(1, Math.max(0, limit));
            st.setInt(2, Math.max(0, offset));
            var rs = st.executeQuery();
            var list = new ArrayList<BankUser>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to get users", e);
        }
    }

    @Override
    public List<BankUser> getUsersByIban(String query, int offset, int limit) {
        String sql = String.format(
                "SELECT * FROM `%s` WHERE `iban` LIKE ? ORDER BY `iban` LIMIT ? OFFSET ?;",
                tableName
        );
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, like(query));
            st.setInt(2, Math.max(0, limit));
            st.setInt(3, Math.max(0, offset));
            var rs = st.executeQuery();
            var list = new ArrayList<BankUser>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new IllegalArgumentException(String.format("Unable to search users by IBAN: %s", query), e);
        }
    }

    @Override
    public List<BankUser> getUsersByLastName(String query, int offset, int limit) {
        String sql = String.format(
                "SELECT * FROM `%s` WHERE `last_name` LIKE ? ORDER BY `last_name`,`first_name` LIMIT ? OFFSET ?;",
                tableName
        );
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, like(query));
            st.setInt(2, Math.max(0, limit));
            st.setInt(3, Math.max(0, offset));
            var rs = st.executeQuery();
            var list = new ArrayList<BankUser>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new IllegalArgumentException(String.format("Unable to search users by last name: %s", query), e);
        }
    }

    @Override
    public int getUsersAmount() {
        String sql = String.format("SELECT COUNT(*) AS cnt FROM `%s`;", tableName);
        try (Connection c = hikariDataSource.getConnection(); PreparedStatement st = c.prepareStatement(sql)) {
            var rs = st.executeQuery();
            rs.next();
            return rs.getInt("cnt");
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to get users amount", e);
        }
    }

    @Override
    public boolean saveUser(BankUser bankUser) {
        String sql = String.format(
                "INSERT INTO `%s` (registration_id, first_name, middle_name, last_name, born_date, address, iban) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "first_name = VALUES(first_name), "
                        + "middle_name = VALUES(middle_name), "
                        + "last_name = VALUES(last_name), "
                        + "born_date = VALUES(born_date), "
                        + "address = VALUES(address), "
                        + "iban = VALUES(iban);",
                tableName
        );
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {

            st.setBytes(1, bankUser.getRegistrationID().toByteArray());
            var d = bankUser.getOwnerData();
            st.setString(2, d.getFirstName());
            st.setString(3, d.getMiddleName());
            st.setString(4, d.getLastName());
            st.setLong(5, d.getBornDate());
            st.setString(6, d.getAddress());
            st.setString(7, bankUser.getIban());
            return st.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    String.format("Unable to save user with id %s",
                            SerializationUtil.getUUID(bankUser.getRegistrationID().toByteArray())), e);
        }
    }

    private static String like(String q) {
        return q == null || q.isBlank() ? "%" : "%" + q + "%";
    }

    private static BankUser mapRow(java.sql.ResultSet rs) throws SQLException {
        return BankUser.newBuilder()
                .setRegistrationID(ByteString.copyFrom(rs.getBytes("registration_id")))
                .setOwnerData(
                        PersonalOwnerData.newBuilder()
                                .setFirstName(rs.getString("first_name"))
                                .setMiddleName(rs.getString("middle_name"))
                                .setLastName(rs.getString("last_name"))
                                .setBornDate(rs.getLong("born_date"))
                                .setAddress(rs.getString("address"))
                                .build()
                )
                .setIban(rs.getString("iban"))
                .build();
    }
}
