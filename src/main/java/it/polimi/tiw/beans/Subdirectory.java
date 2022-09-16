package it.polimi.tiw.beans;

import java.util.Date;

public class Subdirectory extends Directory{
	private int fatherDirectoryId;
	
	public Subdirectory() {
		super();
	}
	
	public Subdirectory(int directoryId, String name, Date creationDate, int fatherDirectoryId, int userId) {
		super(directoryId, name, creationDate, userId);
		this.fatherDirectoryId = fatherDirectoryId;
	}

	public int getFatherDirectoryId() {
		return fatherDirectoryId;
	}

	public void setFatherDirectoryId(int fatherDirectoryId) {
		this.fatherDirectoryId = fatherDirectoryId;
	}
}
