package dev.xpepelok.flowly.database.user.legal;

import com.google.protobuf.ByteString;
import com.zaxxer.hikari.HikariDataSource;
import dev.xpepelok.flowly.configuration.credentials.DatabaseTableProperties;
import dev.xpepelok.flowly.data.model.CompanyData;
import dev.xpepelok.flowly.database.AbstractTable;
import dev.xpepelok.flowly.util.SerializationUtil;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

@Component
@DependsOn("userDataTableImpl")
public class LegalUserDataTableImpl extends AbstractTable implements LegalUserDataTable {

    public LegalUserDataTableImpl(HikariDataSource ds, DatabaseTableProperties props) {
        super(ds, props.getLegalUserDataTableName());

        this.forceQuery(String.format(
                "CREATE TABLE IF NOT EXISTS `%s` ("
                        + "  `registration_id` BINARY(16) PRIMARY KEY, "
                        + "  `company_id` INT, "
                        + "  `company_name` TINYTEXT, "
                        + "  CONSTRAINT `fk_legal_user` FOREIGN KEY (`registration_id`) "
                        + "    REFERENCES `%s` (`registration_id`) "
                        + "    ON DELETE CASCADE ON UPDATE CASCADE"
                        + ") ENGINE=InnoDB;",
                tableName, props.getUserDataTableName()
        ));
    }

    @Override
    public Optional<CompanyData> getCompanyData(byte[] uuid) {
        String sql = String.format("SELECT * FROM `%s` WHERE `registration_id` = ?;", tableName);
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setBytes(1, uuid);
            var rs = st.executeQuery();
            if (!rs.next()) return Optional.empty();
            return Optional.of(
                    CompanyData.newBuilder()
                            .setRegistrationID(ByteString.copyFrom(rs.getBytes("registration_id")))
                            .setCompanyID(rs.getInt("company_id"))
                            .setCompanyName(rs.getString("company_name"))
                            .build()
            );
        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    String.format("Unable get company data for user %s", SerializationUtil.getUUID(uuid)), e);
        }
    }

    @Override
    public boolean saveCompanyData(CompanyData companyData) {
        String sql = String.format(
                "INSERT INTO `%s` (registration_id, company_id, company_name) "
                        + "VALUES (?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "company_id = VALUES(company_id), "
                        + "company_name = VALUES(company_name);",
                tableName
        );
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setBytes(1, companyData.getRegistrationID().toByteArray());
            st.setInt(2, companyData.getCompanyID());
            st.setString(3, companyData.getCompanyName());
            return st.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    String.format("Unable to save legal user with id %s",
                            SerializationUtil.getUUID(companyData.getRegistrationID().toByteArray())), e);
        }
    }
}
