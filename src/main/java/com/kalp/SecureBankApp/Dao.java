package com.kalp.SecureBankApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

@SuppressWarnings("deprecation")
public class Dao 
{
	
	private static DataSource  dataSource;
	
	static
	{
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("oracle.jdbc.driver.OracleDriver"); 
		ds.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");
		ds.setUsername("kalpataru"); 
		ds.setPassword("kalpataru123");
		
		ds.setInitialSize(10); 
		ds.setMaxTotal(50);
		ds.setMinIdle(5);
		ds.setMaxIdle(15);
		ds.setMaxWaitMillis(5000);
		
		ds.setValidationQuery("select 1 from dual");
		ds.setTestOnBorrow(true);
		dataSource = ds;
		
		
	}
private Connection getConnection()	throws SQLException
{
	return dataSource.getConnection();
}
public BankUser validateUser(String username,String password)
{
	String sql = "SELECT user_id, full_name, email, username, password, phone_number, account_number, balance FROM bank_user WHERE LOWER(username)=LOWER(?) and password = ?";

	try(Connection con = getConnection(); 
			PreparedStatement ps = con.prepareStatement(sql)
			)
	{
		ps.setString(1, username);
		ps.setString(2, password);
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			String storedPassword = rs.getString("password");
			if(password.equals(storedPassword))
			{
			BankUser user = new BankUser();
			user.setUserId(rs.getInt("user_id"));
			user.setFullName(rs.getString("full_name"));
			user.setEmail(rs.getString("email"));
			user.setUsername(rs.getString("username"));
			user.setPassword(password);
			user.setPhoneNumber(rs.getString("phone_number"));
			user.setAccountNumber(rs.getString("account_number"));
			user.setBalance(rs.getDouble("balance"));
			return user;
		}
	}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return null;
}

public BankUser findUserByEmail(String email) {

	String sql = "SELECT user_id, full_name, email, username, password, phone_number, account_number, balance " + "FROM bank_user WHERE LOWER(email)=LOWER(?)";
	try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql))
	{ 
		ps.setString(1, email);
		ResultSet rs = ps.executeQuery(); 
		if (rs.next())
		{ 
			BankUser user = new BankUser();
			user.setUserId(rs.getInt("user_id")); 
			user.setFullName(rs.getString("full_name")); 
			user.setEmail(rs.getString("email")); 
			user.setUsername(rs.getString("username")); 
			user.setPassword(rs.getString("password")); 
			user.setPhoneNumber(rs.getString("phone_number")); 
			user.setAccountNumber(rs.getString("account_number")); 
			user.setBalance(rs.getDouble("balance"));
			return user;
			}
		} 
	catch (SQLException e) 
	{
		e.printStackTrace();
		} 
	return null; 
	}

public boolean registerUser(BankUser user)
{
	String checkSql = "SELECT COUNT(*) FROM bank_user WHERE email=? OR username=?"; 
	try (Connection con = getConnection(); 
	PreparedStatement checkPs = con.prepareStatement(checkSql)) 
	{ 
		checkPs.setString(1, user.getEmail()); 
		checkPs.setString(2, user.getUsername());
		ResultSet rs = checkPs.executeQuery(); 
		if (rs.next() && rs.getInt(1) > 0) 
		{ 
			System.out.println("Duplicate email/username detected."); 
			return false;
			} } 
	catch (SQLException e)
	{ 
		e.printStackTrace(); 
		return false; 
		}
	
	String sql = "insert into bank_user ( full_name, email, username,password, phone_number, account_number, balance,created_at)  values(?,?,?,?,?,?,?,sysdate)";
	try(Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql);)
	{
		ps.setString(1, user.getFullName());
		ps.setString(2, user.getEmail());
		ps.setString(3, user.getUsername());
		ps.setString(4,user.getPassword());
		ps.setString(5, user.getPhoneNumber());
		ps.setString(6, user.getAccountNumber());
		ps.setDouble(7, user.getBalance());
		return ps.executeUpdate() == 1;
	}
	catch(SQLException  e)
	{
		if(e.getErrorCode() == 1)
		{
			return false;
		}
		e.printStackTrace();
	}
	return false;
}

public boolean updateBalance(String accountNumber, double newBalance)
{
	String sql = "update bank_user set balance = ? where account_number = ?";
	try(Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql);)
	{
		
		ps.setDouble(1, newBalance);
		ps.setString(2, accountNumber);
		int rows = ps.executeUpdate();
		return rows>0;
	}
	catch(SQLException e)
	{
		e.printStackTrace();
	}
	return false;
}

public boolean updateUserPassword(String email, String newPassword)
{
	String sql = "UPDATE bank_user SET password = ? WHERE LOWER(email) = LOWER(?)"; 
	try (Connection con = getConnection(); 
			PreparedStatement ps = con.prepareStatement(sql))
	{ 
		ps.setString(1,newPassword); // ✅ secure hashing
		ps.setString(2, email); 
		return ps.executeUpdate() > 0; 
		} catch (SQLException e)
	{ 
			e.printStackTrace();
			}
	return false;
	}
}


