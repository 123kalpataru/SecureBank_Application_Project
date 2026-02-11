package com.kalp.SecureBankApp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class TransactionDao {
    private static DataSource dataSource;

    static {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        ds.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");
        ds.setUsername("kalpataru");
        ds.setPassword("kalpataru123");

        // ✅ Connection pool tuning
        ds.setInitialSize(10);
        ds.setMaxTotal(50);
        ds.setMinIdle(5);
        ds.setMaxIdle(15);
        ds.setMaxWaitMillis(5000);
        ds.setValidationQuery("SELECT 1 FROM dual");
        ds.setTestOnBorrow(true);

        dataSource = ds;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // ✅ Add transaction (with connection)
    private boolean addTransaction(Transaction txn, Connection con) throws SQLException {
        String sql = "INSERT INTO transactions (ACCOUNT_NUMBER, TXN_TYPE, AMOUNT, STATUS, TXN_DATE) " +
                     "VALUES (?, ?, ?, ?, SYSDATE)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, txn.getAccountNumber());
            ps.setString(2, txn.getTxnType());
            ps.setDouble(3, txn.getAmount());
            ps.setString(4, txn.getStatus());
            return ps.executeUpdate() == 1;
        }
    }

    // ✅ Update balance
    private boolean updateBalance(String accountNumber, double newBalance, Connection con) throws SQLException {
        String sql = "UPDATE bank_user SET BALANCE=? WHERE ACCOUNT_NUMBER=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountNumber);
            return ps.executeUpdate() == 1;
        }
    }

    // ✅ Get balance
    public double getBalance(String accountNumber) {
        String sql = "SELECT BALANCE FROM bank_user WHERE ACCOUNT_NUMBER=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching balance: " + e.getMessage());
        }
        return 0.0;
    }

    // ✅ Deposit
    public boolean deposit(String accountNumber, double amount) {
        double currentBalance = getBalance(accountNumber);
        double newBalance = currentBalance + amount;

        Transaction txn = new Transaction();
        txn.setAccountNumber(accountNumber);
        txn.setTxnType("DEPOSIT");
        txn.setAmount(amount);
        txn.setStatus("SUCCESS");

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            boolean success = addTransaction(txn, conn) && updateBalance(accountNumber, newBalance, conn);
            if (success) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Deposit failed: " + e.getMessage());
            return false;
        }
    }

    // ✅ Withdraw
    public boolean withdraw(String accountNumber, double amount) {
        double currentBalance = getBalance(accountNumber);

        if (amount > currentBalance) {
            System.out.println("Withdrawal failed: insufficient funds.");
            return false;
        }

        double newBalance = currentBalance - amount;

        Transaction txn = new Transaction();
        txn.setAccountNumber(accountNumber);
        txn.setAmount(amount);
        txn.setTxnType("WITHDRAWAL");
        txn.setStatus("SUCCESS");

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            boolean success = addTransaction(txn, conn) && updateBalance(accountNumber, newBalance, conn);
            if (success) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Withdrawal failed: " + e.getMessage());
            return false;
        }
    }

    // ✅ Paginated transaction history
    public List<Transaction> getTransactionHistory(String accountNumber, int page, int pageSize) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE ACCOUNT_NUMBER=? " +
                     "ORDER BY TXN_DATE DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.setInt(2, (page - 1) * pageSize); // OFFSET
            ps.setInt(3, pageSize);              // FETCH NEXT
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching paginated history: " + e.getMessage());
        }
        return list;
    }

    // ✅ Full transaction history (for CSV download)
    public List<Transaction> getTransactionHistory(String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE ACCOUNT_NUMBER=? ORDER BY TXN_DATE DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching full history: " + e.getMessage());
        }
        return list;
    }

    // ✅ Count transactions
    public int countTransactions(String accountNumber) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE ACCOUNT_NUMBER=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting transactions: " + e.getMessage());
        }
        return 0;
    }

    // ✅ Delete transaction
    public boolean deleteTransaction(int txnId) {
        String sql = "DELETE FROM transactions WHERE TXN_ID=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, txnId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
            return false;
        }
    }

    // ✅ Map row
    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction txn = new Transaction();
        txn.setTxnId(rs.getInt("TXN_ID"));
        txn.setAccountNumber(rs.getString("ACCOUNT_NUMBER"));
        txn.setTxnType(rs.getString("TXN_TYPE"));
        txn.setAmount(rs.getDouble("AMOUNT"));
        txn.setStatus(rs.getString("STATUS"));
        txn.setTxnDate(rs.getTimestamp("TXN_DATE"));
        return txn;
    }
}
