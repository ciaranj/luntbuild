package net.mccg.lunt;

public class LuntProject {
	private String luntUsername;
	private String luntPassword;
	private String luntServer;
	private String project;
	private String schedule;
	private String artifact;

	public LuntProject(String luntUsername, String luntPassword, String luntServer, String project, String schedule, String artifact) {
		this.luntUsername = luntUsername;
		this.luntPassword = luntPassword;
		this.luntServer = luntServer;
		this.project = project;
		this.schedule = schedule;
		this.artifact = artifact;
	}

	public String getLuntUsername() {
		return luntUsername;
	}
	
	public void setLuntUsername(String luntUsername) {
		this.luntUsername = luntUsername;
	}
	
	public String getLuntPassword() {
		return luntPassword;
	}
	
	public void setLuntPassword(String luntPassword) {
		this.luntPassword = luntPassword;
	}
	
	public String getLuntServer() {
		return luntServer;
	}
	
	public void setLuntServer(String luntServer) {
		this.luntServer = luntServer;
	}
	
	public String getProject() {
		return project;
	}
	
	public void setProject(String project) {
		this.project = project;
	}
	
	public String getSchedule() {
		return schedule;
	}
	
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}
}
