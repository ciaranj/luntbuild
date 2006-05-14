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
 * This class is used to stands for a search criteria when searching for builds
 *
 * @author robin shine
 */
public class SearchCriteria {
	/**
	 * Version string of the matching builds, can be part of a version string when use
	 * non-exact match
	 */
	private String version;
	/**
	 * Specifies whether or not to perform exact match against version property
	 */
	private boolean exactMatch;

	/**
	 * Specifies the build status of matching builds
	 */
	private int status;

	/**
	 * Specifies the start date of matching builds
	 */
	private Date from;
	/**
	 * Specifies the end date of matching builds
	 */
	private Date to;

	/**
	 * Specifies schedule ids matching builds should belong to.
	 */
	private long scheduleIds[];

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
