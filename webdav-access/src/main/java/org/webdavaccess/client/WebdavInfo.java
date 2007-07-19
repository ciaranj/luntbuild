package org.webdavaccess.client;

import java.util.Date;

public class WebdavInfo {

	String name;
	String displayName;
	Date creationDate;
	Date lastModifiedDate;
	long length;
	String type;
	String path;
	String owner;
	boolean isFolder;
	
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public boolean isFolder() {
		return isFolder;
	}
	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String print() {
		StringBuilder buf = new StringBuilder();
		buf.append(isFolder ? "D " : "  ");
		buf.append(path);
		buf.append(": ");
		buf.append(name);
		buf.append(" cr:");
		buf.append(creationDate.toString());
		buf.append(" mod:");
		buf.append(lastModifiedDate.toString());
		buf.append(" len:");
		buf.append(length);
		buf.append(" t:");
		buf.append(type);
		buf.append(" o:");
		buf.append(owner);
		return buf.toString();
	}

}
