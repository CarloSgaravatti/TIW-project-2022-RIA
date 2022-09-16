package it.polimi.tiw.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.Subdirectory;
import it.polimi.tiw.utils.Pair;

public class DocumentDAO {
	private Connection connection;
	
	public DocumentDAO(Connection connection) {
		super();
		this.connection = connection;
	}
	
	public void createDocument(String name, Date creationDate, String summary, int subdirectoryId, String type) throws SQLException {
		String query = "INSERT INTO document (name,creation_date,summary,subdirectory, type) VALUES (?,?,?,?,?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, name);
			pstatement.setDate(2, creationDate);
			pstatement.setString(3, summary);
			pstatement.setInt(4, subdirectoryId);
			pstatement.setString(5,  type);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}
	
	public Document findDocument(int documentId) throws SQLException {
		Document document = null;
		String query = "SELECT * FROM document WHERE documentid = ?";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, documentId);
			res = pstatement.executeQuery();
			if (!res.next()) return null;
			String name = res.getString("name");
			String summary = res.getString("summary");
			Date creationDate = res.getDate("creation_date");
			int subdirectoryId = res.getInt("subdirectory");
			String type = res.getString("type");
			document = new Document(documentId, name, creationDate, summary, subdirectoryId, type);
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
		return document;
	}
	
	public Pair<Document, Subdirectory> findDocumentAndSubdirectory(int documentId) throws SQLException {
		Pair<Document, Subdirectory> result = null;
		String query = "SELECT * FROM document JOIN directory ON document.subdirectory = directory.directoryid WHERE " + "document.documentid = ?";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, documentId);
			res = pstatement.executeQuery();
			if (!res.next()) return null;
			String name = res.getString("document.name");
			String summary = res.getString("document.summary");
			Date creationDate = res.getDate("document.creation_date");
			int subdirectoryId = res.getInt("document.subdirectory");
			String type = res.getString("document.type");
			Document document = new Document(documentId, name, creationDate, summary, subdirectoryId, type);
			String subdirectoryName = res.getString("directory.name");
			Date subdirectoryCreationDate = res.getDate("directory.creation_date");
			int fatherDirectoryId = res.getInt("directory.father_directory");
			int subdirectoryUserId = res.getInt("directory.user");
			Subdirectory subdirectory = new Subdirectory(subdirectoryId, subdirectoryName, subdirectoryCreationDate, fatherDirectoryId, subdirectoryUserId);
			result = new Pair<>(document, subdirectory);
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
		return result;
	}
	
	public void moveDocument(int documentId, int subdirectoryId) throws SQLException {
		String query = "UPDATE document SET subdirectory = ? WHERE documentid = ?";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, subdirectoryId);
			pstatement.setInt(2, documentId);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}
	
	public List<Document> findAlldocumentsOfSubdirectory(int subdirectoryId) throws SQLException {
		List<Document> documents = null;
		String query = "SELECT * FROM document WHERE subdirectory = ? ORDER BY name";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, subdirectoryId);
			res = pstatement.executeQuery();
			documents = new ArrayList<>();
			while (res.next()) {
				int documentId = res.getInt("documentid");
				String name = res.getString("name");
				String summary = res.getString("summary");
				Date creationDate = res.getDate("creation_date");
				String type = res.getString("type");
				documents.add(new Document(documentId, name, creationDate, summary, subdirectoryId, type));
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
		return documents;
	}
	
	public Document findDocumentFromNameAndSubdirectory(String docName, int subdirectoryId) throws SQLException {
		Document document = null;
		String query = "SELECT * FROM document WHERE name = ? AND subdirectory = ?";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, docName);
			pstatement.setInt(2, subdirectoryId);
			res = pstatement.executeQuery();
			if (!res.next()) return null;
			int id = res.getInt("documentId");
			String summary = res.getString("summary");
			Date creationDate = res.getDate("creation_date");
			String type = res.getString("type");
			document = new Document(id, docName, creationDate, summary, subdirectoryId, type);
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
		return document;
	}
	
	public void deleteDocument(int documentId) throws SQLException {
		String query = "DELETE FROM document WHERE documentid = ?";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, documentId);
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