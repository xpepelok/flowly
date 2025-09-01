package dev.xpepelok.flowly.database.transaction;

import com.zaxxer.hikari.HikariDataSource;
import dev.xpepelok.flowly.configuration.credentials.DatabaseTableProperties;
import dev.xpepelok.flowly.database.AbstractTable;
import dev.xpepelok.flowly.transaction.model.Transaction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionTableImpl extends AbstractTable implements TransactionTable {

    public TransactionTableImpl(HikariDataSource hikariDataSource, DatabaseTableProperties databaseTableProperties) {
        super(hikariDataSource, databaseTableProperties.getTransactionTableName());

        this.forceQuery(String.format(
                "CREATE TABLE IF NOT EXISTS `%s` ("
                        + "`recipient` VARCHAR(34) NOT NULL, "
                        + "`sender` VARCHAR(34) NOT NULL, "
                        + "`sum` BIGINT NOT NULL, "
                        + "`date` BIGINT NOT NULL, "
                        + "INDEX `idx_recipient` (`recipient`), "
                        + "INDEX `idx_sender` (`sender`), "
                        + "INDEX `idx_date` (`date`)"
                        + ");", tableName
        ));
    }

    @Override
    public boolean addTransaction(Transaction transaction) {
        String sql = String.format(
                "INSERT INTO `%s` (`recipient`,`sender`,`sum`,`date`) VALUES (?,?,?,?);",
                tableName
        );
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, transaction.getRecipient());
            st.setString(2, transaction.getSender());
            st.setLong(3, transaction.getSum());
            st.setLong(4, transaction.getTransactionDate());
            return st.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    String.format("Unable to save transaction: {sender=%s, recipient=%s, sum=%d, date=%d}",
                            transaction.getSender(), transaction.getRecipient(),
                            transaction.getSum(), transaction.getTransactionDate()), e);
        }
    }

    @Override
    public List<Transaction> getTransactionsBySender(String iban, int offset, int limit) {
        String sql = String.format(
                "SELECT `recipient`,`sender`,`sum`,`date` FROM `%s` WHERE `sender`=? ORDER BY `date` DESC",
                tableName
        );
        return executeQuery(sql, offset, limit, iban);
    }

    @Override
    public List<Transaction> getTransactionsByRecipient(String iban, int offset, int limit) {
        String sql = String.format(
                "SELECT `recipient`,`sender`,`sum`,`date` FROM `%s` WHERE `recipient`=? ORDER BY `date` DESC",
                tableName
        );
        return executeQuery(sql, offset, limit, iban);
    }

    @Override
    public List<Transaction> getTransactionsForIban(String iban, int offset, int limit) {
        String sql = String.format(
                "SELECT `recipient`,`sender`,`sum`,`date` FROM `%s` WHERE `recipient`=? "
                        + "UNION ALL "
                        + "SELECT `recipient`,`sender`,`sum`,`date` FROM `%s` WHERE `sender`=? "
                        + "ORDER BY `date` DESC",
                tableName, tableName
        );
        return executeQuery(sql, offset, limit, iban, iban);
    }

    @Override
    public List<Transaction> getTransactions(int offset, int limit) {
        String sql = String.format(
                "SELECT `recipient`,`sender`,`sum`,`date` FROM `%s` ORDER BY `date` DESC",
                tableName
        );
        return executeQuery(sql, offset, limit);
    }

    @Override
    public int getTransactionsAmount() {
        String sql = String.format("SELECT COUNT(*) AS cnt FROM `%s`;", tableName);
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            var rs = st.executeQuery();
            rs.next();
            return rs.getInt("cnt");
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to get transactions amount", e);
        }
    }

    @Override
    public long getIncomingSum(String iban) {
        String sql = String.format("SELECT COALESCE(SUM(`sum`),0) AS s FROM `%s` WHERE `recipient`=?;", tableName);
        return singleLongQuery(sql, iban);
    }

    @Override
    public long getOutgoingSum(String iban) {
        String sql = String.format("SELECT COALESCE(SUM(`sum`),0) AS s FROM `%s` WHERE `sender`=?;", tableName);
        return singleLongQuery(sql, iban);
    }

    private List<Transaction> executeQuery(String baseSql, int offset, int limit, Object... params) {
        final boolean noLimit = limit <= 0;
        String sql = noLimit ? (baseSql + ";") : (baseSql + " LIMIT ? OFFSET ?;");

        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            int idx = 1;
            for (Object param : params) {
                st.setObject(idx++, param);
            }
            if (!noLimit) {
                st.setInt(idx++, Math.max(0, limit));
                st.setInt(idx, Math.max(0, offset));
            }
            var rs = st.executeQuery();
            var list = new ArrayList<Transaction>();
            while (rs.next()) {
                list.add(
                        Transaction.newBuilder()
                                .setRecipient(rs.getString("recipient"))
                                .setSender(rs.getString("sender"))
                                .setSum(rs.getLong("sum"))
                                .setTransactionDate(rs.getLong("date"))
                                .build()
                );
            }
            return list;
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to execute transaction query", e);
        }
    }

    private long singleLongQuery(String sql, String param) {
        try (Connection c = hikariDataSource.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, param);
            var rs = st.executeQuery();
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to execute aggregate query", e);
        }
    }
}
