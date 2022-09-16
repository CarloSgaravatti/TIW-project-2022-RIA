package it.polimi.tiw.beans;

import java.util.Date;

public class Document {
	private int documentId;
	private String name;
	private Date creationDate;
	private String summary;
	private int subdirectoryId;
	private String documentType;
	
	public Document() {
		super();
	}
	
	public Document(int documentId, String name, Date creationDate, String summary, int subdirectoryId, String documentType) {
		super();
		this.documentId = documentId;
		this.name = name;
		this.creationDate = creationDate;
		this.summary = summary;
		this.subdirectoryId = subdirectoryId;
		this.documentType = documentType;
	}

	public int getDocumentId() {
		return documentId;
	}
	
	public String getName() {
		return name;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public int getSubdirectoryId() {
		return subdirectoryId;
	}
	
	public String getDocumentType() {
		return documentType;
	}
	
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public void setSubdirectoryId(int subdirectoryId) {
		this.subdirectoryId = subdirectoryId;
	}
	
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
}
