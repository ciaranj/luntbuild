/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-20
 * Time: 13:18:04
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
package com.luntsys.luntbuild.db;

import java.util.Date;

/**
 * This class mapps to a hibernate entity. It represents a
 * execution result of a particular build schedule
 *
 * @author alvin shen
 */
public class Build {
	/**
	 * These constants represent status of this build
	 */
	public static final int NOT_FINISHED = 0;
	public static final int SUCCESS = 1;
	public static final int FAILED = 2;
	
	public static final String SCHEDULE_TYPE_NOW = "now";
	public static final String SCHEDULE_TYPE_AFTER = "after";
	public static final String SCHEDULE_TYPE_AT = "at";

	private long id;

	/**
	 * Status of current build,
	 */
	private int status;

	/**
	 * The date when this build is started
	 */
	private Date startDate;

	/**
	 * The date when this build is finished
	 */
	private Date endDate;

	/**
	 * Version number of this build
	 */
	private String version;

	public boolean isLabel() {
		return label;
	}

	public void setLabel(boolean label) {
		this.label = label;
	}

	/**
	 * Does this build have a corresponding label in version control system?
	 */
	private boolean label;

	/**
	 * Is this build a rebuild?
	 */
	private boolean rebuild;

	private BuildSchedule buildSchedule;

	public long getId() {
		return id;
	}

	public boolean isRebuild() {
		return rebuild;
	}

	public void setRebuild(boolean rebuild) {
		this.rebuild = rebuild;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public BuildSchedule getBuildSchedule() {
		return buildSchedule;
	}

	public void setBuildSchedule(BuildSchedule buildSchedule) {
		this.buildSchedule = buildSchedule;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Build){
			if (getId() == ((Build)obj).getId())
				return true;
		}
		return false;
	}

	public int hashCode() {
		return (int) getId();
	}
}
