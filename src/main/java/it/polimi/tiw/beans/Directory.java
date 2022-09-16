package it.polimi.tiw.beans;

import java.util.Date;
import java.util.List;

public class Directory {
	private int directoryId;
	private String name;
	private Date creationDate;
	private int userId;
	private List<Subdirectory> subdirectories;
	
	public Directory() {
		super();
	}
	
	public Directory(int directoryId, String name, Date creationDate, int userId) {
		super();
		this.directoryId = directoryId;
		this.name = name;
		this.creationDate = creationDate;
		this.userId = userId;
	}
	
	public int getDirectoryId() {
		return directoryId;
	}

	public String getName() {
		return name;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	

	public int getUserId() {
		return userId;
	}
	
	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public List<Subdirectory> getSubdirectories() {
		return subdirectories;
	}

	public void setSubdirectories(List<Subdirectory> subdirectories) {
		this.subdirectories = subdirectories;
	}
}
