package com.luntsys.luntbuild.facades;


import java.util.Date;

/**
 * This class is used to stands for a search criteria when searching for builds
 *
 * @author robin shine
 */
public class SearchCriteria {
	/**
	 * Version string of the matching builds, can be part of a version string when use
	 * non-exact match
	 */
	private String version = "";
	/**
	 * Specifies whether or not to perform exact match against version property
	 */
	private boolean exactMatch = true;

	/**
	 * Specifies the build status of matching builds
	 */
	private int status = Constants.BUILD_STATUS_ALL;

	/**
	 * Specifies the start date of matching builds
	 */
	private Date from = null;
	/**
	 * Specifies the end date of matching builds
	 */
	private Date to = null;

	/**
	 * Specifies schedule ids matching builds should belong to.
	 */
	private long scheduleIds[] = new long[0];

	/**
	 * Get version to search
	 * @return version to search
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Get whether or not perform exact match on version when searches build
	 * @return boolean value
	 */
	public boolean isExactMatch() {
		return exactMatch;
	}

	/**
	 * Set whether or not perform exact match on version when searches build
	 * @param exactMatch
	 */
	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}

	/**
	 * Set version to search
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Get start date to search builds on
	 * @return start date to search builds on
	 */
	public Date getFrom() {
		return from;
	}

	/**
	 * Set start date to search builds on
	 * @param from
	 */
	public void setFrom(Date from) {
		this.from = from;
	}

	/**
	 * Get end date to search builds on
	 * @return end date to search builds on
	 */
	public Date getTo() {
		return to;
	}

	/**
	 * Set end date to search builds on
	 * @param to
	 */
	public void setTo(Date to) {
		this.to = to;
	}

	/**
	 * Get build status to search
	 * @return one value of {@link com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_ALL},
	 * {@link com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_FAILED},
	 * {@link com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_RUNNING},
	 * {@link com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_SUCCESS}
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set build status to search
	 * @param status refer to return value of {@link #getStatus()}
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Get id array of schedules the search should be performed in
	 * @return id array of schedules
	 */
	public long[] getScheduleIds() {
		return scheduleIds;
	}

	/**
	 * Set id array of schedules
	 * @param scheduleIds
	 */
	public void setScheduleIds(long[] scheduleIds) {
		this.scheduleIds = scheduleIds;
	}
}
