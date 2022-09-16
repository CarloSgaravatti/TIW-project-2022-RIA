package it.polimi.tiw.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

import it.polimi.tiw.beans.Subdirectory;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Directory;

public class DirectoryDAO {
	private Connection connection;

	public DirectoryDAO(Connection connection) {
		super();
		this.connection = connection;
	}
	
	public void createDirectory(String name, Date creationDate, int userId) throws SQLException {
		String query = "INSERT INTO directory (name,creation_date,is_subdirectory,user) "
				+ "VALUES (?,?,0,?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, name);
			pstatement.setDate(2, creationDate);
			pstatement.setInt(3, userId);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
	}

	public void createSubdirectory(String name, Date creationDate, int fatherId, int userId) throws SQLException {
		String query = "INSERT INTO directory (name,creation_date,is_subdirectory,father_directory,user) "
				+ "VALUES (?,?,1,?,?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, name);
			pstatement.setDate(2, creationDate);
			pstatement.setInt(3, fatherId);
			pstatement.setInt(4, userId);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
	}
	
	//Find both Directory and Subdirectory
	public Directory findDirectoryById(int directoryId) throws SQLException {
		Directory directory = null;
		String query = "SELECT * FROM directory WHERE directoryid = ?";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, directoryId);
			res = pstatement.executeQuery();
			if (!res.next()) return null;
			String directoryName = res.getString("name");
			Date directoryCreationDate = res.getDate("creation_date");
			int fatherDirectoryId = res.getInt("father_directory");
			int directoryUserId = res.getInt("user");
			directory = (res.getInt("is_subdirectory") == 0) ? new Directory(directoryId, directoryName, directoryCreationDate, directoryUserId)
					: new Subdirectory(directoryId, directoryName, directoryCreationDate, fatherDirectoryId, directoryUserId);
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
		return directory;
	}
	
	public List<Directory> findDirectoriesAndSubdirectoriesOfUser(User user) throws SQLException {
		List<Directory> userDirectoryTree = new ArrayList<>();
		String query = "SELECT * FROM directory WHERE user = ? AND is_subdirectory = '0' ORDER BY name";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, user.getUserId());
			res = pstatement.executeQuery();
			while (res.next()) {
				int directoryId = res.getInt("directoryid");
				String directoryName = res.getString("name");
				Date directoryCreationDate = res.getDate("creation_date");
				int directoryUserId = res.getInt("user");
				List<Subdirectory> subdirectories = findAllSubdirectoriesOfDirectory(directoryId);
				Directory directory = new Directory(directoryId, directoryName, directoryCreationDate, directoryUserId);
				directory.setSubdirectories(subdirectories);
				userDirectoryTree.add(directory);
			}
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
		return userDirectoryTree;
	}
	
	private List<Subdirectory> findAllSubdirectoriesOfDirectory(int directoryId) throws SQLException {
		List<Subdirectory> subdirectories = new ArrayList<>();
		String query = "SELECT * FROM directory WHERE is_subdirectory = '1' AND father_directory = ? " +
							"ORDER BY name";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, directoryId);
			res = pstatement.executeQuery();
			while (res.next()) {
				int subdirectoryId = res.getInt("directoryid");
				String directoryName = res.getString("name");
				Date directoryCreationDate = res.getDate("creation_date");
				int fatherDirectoryId = res.getInt("father_directory");
				int userId = res.getInt("user");
				subdirectories.add(new Subdirectory(subdirectoryId, directoryName, directoryCreationDate, fatherDirectoryId, userId));
			}
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
		
		return subdirectories;
	}
	
	public List<Directory> getAllDirectories(User user) throws SQLException {
		List<Directory> directories = new ArrayList<>();
		String query = "SELECT * FROM directory WHERE user = ? AND is_subdirectory = '0'";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, user.getUserId());
			res = pstatement.executeQuery();
			while(res.next()) {
				int id = res.getInt("directoryid");
				String name = res.getString("name");
				Date creationDate = res.getDate("creation_date");
				int userId = res.getInt("user");
				directories.add(new Directory(id, name, creationDate, userId));
			}
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
		return directories;
	}
	
	public List<Subdirectory> getAllSubirectories(User user) throws SQLException {
		List<Subdirectory> subdirectories = new ArrayList<>();
		String query = "SELECT * FROM directory WHERE user = ? AND is_subdirectory = '1'";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, user.getUserId());
			res = pstatement.executeQuery();
			while(res.next()) {
				int id = res.getInt("directoryid");
				String name = res.getString("name");
				Date creationDate = res.getDate("creation_date");
				int father = res.getInt("father_directory");
				int userId = res.getInt("user");
				subdirectories.add(new Subdirectory(id, name, creationDate, father, userId));
			}
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
		return subdirectories;
	}
	
	public Directory findDirectoryByNameAndUser(String directoryName, int userId) throws SQLException{
		Directory directory = null;
		String query = "SELECT * FROM directory WHERE name = ? AND user = ?";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, directoryName);
			pstatement.setInt(2, userId);
			res = pstatement.executeQuery();
			if (!res.next()) return null;
			int id = res.getInt("directoryid");
			String name = res.getString("name");
			Date creationDate = res.getDate("creation_date");
			directory = new Directory(id, name, creationDate, userId);
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
		return directory;
	}
	
	public Subdirectory findSubdirectoryByNameAndFatherDirectory(String subdirectoryName, int fatherDirectoryId) throws SQLException{
		Subdirectory subdirectory = null;
		String query = "SELECT * FROM directory WHERE name = ? AND father_directory = ?";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, subdirectoryName);
			pstatement.setInt(2, fatherDirectoryId);
			res = pstatement.executeQuery();
			if (!res.next()) return null;
			int id = res.getInt("directoryid");
			String name = res.getString("name");
			Date creationDate = res.getDate("creation_date");
			int userId = res.getInt("user");
			subdirectory = new Subdirectory(id, name, creationDate, fatherDirectoryId,  userId);
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
		return subdirectory;
	}
	
	//Can delete both directory and subdirectory
	public void deleteDirectory(int directoryId) throws SQLException {
		String query = "DELETE FROM directory WHERE directoryid = ?";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, directoryId);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
	}
}
