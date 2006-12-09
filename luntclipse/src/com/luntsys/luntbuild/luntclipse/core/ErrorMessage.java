package com.luntsys.luntbuild.luntclipse.core;

import java.util.Date;

/**
 * The Class ErrorMessage.
 */
public class ErrorMessage extends NotificationMessage {

	/**
	 * The Constructor.
	 *
	 * @param message the message
	 */
	public ErrorMessage(String message) {
		super();
		this.setContent(message);
		this.setDate(new Date());
		this.setSeverity(SeverityLevel.Error);
	}

	public ErrorMessage(String projectName, String message) {
		super();
		this.setBuildVersion(projectName);
		this.setContent(message);
		this.setDate(new Date());
		this.setSeverity(SeverityLevel.Error);
	}

	public ErrorMessage(String projectName, String scheduleName, String message) {
		super();
		this.setBuildVersion(projectName + "/" + scheduleName);
		this.setContent(message);
		this.setDate(new Date());
		this.setSeverity(SeverityLevel.Error);
	}

	public ErrorMessage(String projectName, String scheduleName, String buildVersion, String message) {
		super();
		this.setBuildVersion(projectName + "/" + scheduleName+ "/" + buildVersion);
		this.setContent(message);
		this.setDate(new Date());
		this.setSeverity(SeverityLevel.Error);
	}
}
