/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-9-2
 * Time: 15:29:25
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.luntsys.luntbuild.facades;

import java.util.Date;

/**
 * This class is used to stands for a search criteria when searching for builds.
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
	 * Gets the version to search for.
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Checks if search should perform exact matching on version when searching for builds.
	 * 
	 * @return <code>true</code> if only exact matches should be returned
	 */
	public boolean isExactMatch() {
		return exactMatch;
	}

	/**
	 * Sets the exact matching setting for search.
	 * 
	 * @param exactMatch if <code>true</code>, only exact matches should be returned
	 */
	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}

	/**
	 * Sets the version to search for.
	 * 
	 * @param version the version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the start date to search for builds from.
	 * 
	 * @return the start date
	 */
	public Date getFrom() {
		return from;
	}

	/**
	 * Sets the start date to search for builds from.
	 * 
	 * @param from the start date
	 */
	public void setFrom(Date from) {
		this.from = from;
	}

	/**
	 * Gets the end date to search for builds to.
	 * 
	 * @return the end date
	 */
	public Date getTo() {
		return to;
	}

	/**
	 * Sets the end date to search for builds to.
	 * 
	 * @param to the end date
	 */
	public void setTo(Date to) {
		this.to = to;
	}

	/**
	 * Gets the build status to search for.
	 * 
	 * @return the build status
	 * @see Constants#BUILD_STATUS_ALL
	 * @see Constants#BUILD_STATUS_FAILED
	 * @see Constants#BUILD_STATUS_RUNNING
	 * @see Constants#BUILD_STATUS_SUCCESS
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Sets the build status to search for.
	 * 
	 * @param status the build status
	 * @see Constants#BUILD_STATUS_ALL
	 * @see Constants#BUILD_STATUS_FAILED
	 * @see Constants#BUILD_STATUS_RUNNING
	 * @see Constants#BUILD_STATUS_SUCCESS
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Gets the array of identifiers for the schedules the search should be performed in.
	 * 
	 * @return the array of schedule identifiers
	 */
	public long[] getScheduleIds() {
		return scheduleIds;
	}

	/**
	 * Sets the array of identifiers for the schedules the search should be performed in.
	 * 
	 * @param scheduleIds the array of schedule identifiers
	 */
	public void setScheduleIds(long[] scheduleIds) {
		this.scheduleIds = scheduleIds;
	}
}
