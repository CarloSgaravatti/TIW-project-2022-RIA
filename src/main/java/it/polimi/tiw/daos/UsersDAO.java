package it.polimi.tiw.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import it.polimi.tiw.beans.User;

public class UsersDAO {
	private Connection connection;
	
	public UsersDAO(Connection connection) {
		super();
		this.connection = connection;
	}
	
	public User createUser(String username, String email, String password) throws SQLException {
		User user = null;
		String query = "INSERT INTO user (username, password, email) VALUES (?,?,?)";
		PreparedStatement pstatement = null;
		ResultSet keys = null;
		try {
			pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			pstatement.setString(3, email);
			pstatement.executeUpdate();
			keys = pstatement.getGeneratedKeys();
			boolean userCreated = keys.next();
			if (!userCreated) {
				return null;
			}
			int userId = keys.getInt(1);
			user = new User(userId, username, email);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (keys != null) {
					keys.close();
				}
			} catch (Exception e) {
				throw new SQLException(e);
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
		return user;
	}
	
	public User checkCredentials(String username, String password) throws SQLException {
		User user = null;
		String query = "SELECT * FROM user WHERE username = ? AND password = ?";
		System.out.println(username);
		System.out.println(password);
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			res = pstatement.executeQuery();
			boolean userPresent = res.next();
			if (!userPresent) {
				return null;
			}
			int userId = res.getInt("userid");
			String email = res.getString("email");
			user = new User(userId, username, email);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (res != null) {
					res.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return user;
	}

}
