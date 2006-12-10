package com.luntsys.luntbuild.luntclipse.core;

import java.util.Date;

/**
 * The Class NotificationMessage.
 */
public class NotificationMessage {

	/** The url. */
	private String url = null;

	private Date date = null;

	/** The content. */
	private String content = null;

	private String buildVersion = null;

	/**
	 * The Enum SeverityLevel.
	 */
	public enum SeverityLevel {

		/** The Error. */
		Error,

		/** The Warnining. */
		Warning,

		/** The Info. */
		Info,

		/** The Debug. */
		Debug
	};

	/** The severity. */
	private SeverityLevel severity = SeverityLevel.Info;

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Gets the severity.
	 *
	 * @return the severity
	 */
	public SeverityLevel getSeverity() {
		return severity;
	}

	/**
	 * Sets the severity.
	 *
	 * @param severity the severity
	 */
	public void setSeverity(SeverityLevel severity) {
		this.severity = severity;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return Returns the buildVersion.
	 */
	public final String getBuildVersion() {
		return buildVersion;
	}

	/**
	 * @param buildVersion The buildVersion to set.
	 */
	public final void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

}
