/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 9:01:21
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

package com.luntsys.luntbuild.facades.lb111;

import java.util.Date;

/**
 * Build facade.
 *
 * @author robin shine
 * @see com.luntsys.luntbuild.db.Build
 */
public class BuildFacade {
	/** Build status, unknown */
	public static final int UNKNOWN = 0;
	/** Build status, success */
	public static final int SUCCESS = 1;
	/** Build status, failed */
	public static final int FAILED = 2;
	/** Build status, running */
	public static final int RUNNING  = 3;

	private long id;

	private int status;

	private Date startDate;

	private Date endDate;

	private String version;

	private boolean haveLabelOnHead = false;

	/**
	 * Whether or not this is a clean build
	 */
	private boolean cleanBuild;

	/**
	 * Is this build a rebuild?
	 */
	private boolean rebuild;

	/**
	 * Url to access build information, build log file, and revision log file
	 */
	private transient String url;
	private transient String buildLogUrl;
	private transient String revisionLogUrl;

	private long buildScheduleId;

	/**
	 * Label strategy of this build
	 */
	private String labelStrategy;
	/**
	 * Post-build strategy of this build
	 */
	private String postbuildStrategy;

	/**
	 * Gets the identifer of this build.
	 * 
	 * @return the identifer of this build
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Sets the identifier of this build.
	 *
	 * @param id the identifier of this build
	 */
	public void setId(long id) {
		this.id = id;
	}

    /**
     * Gets the status of this build.
     * 
     * @return the status of this build
     * @see BuildFacade#UNKNOWN
     * @see BuildFacade#SUCCESS
     * @see BuildFacade#FAILED
     * @see BuildFacade#RUNNING
     */
	public int getStatus() {
		return this.status;
	}

    /**
     * Sets the status of this build.
     * 
     * @param status the status of this build
     * @see BuildFacade#UNKNOWN
     * @see BuildFacade#SUCCESS
     * @see BuildFacade#FAILED
     * @see BuildFacade#RUNNING
     */
	public void setStatus(int status) {
		this.status = status;
	}

    /**
     * Gets the starting date of this build.
     * 
     * @return the starting date of this build
     */
	public Date getStartDate() {
		return this.startDate;
	}

    /**
     * Sets the start date of this build.
     * 
     * @param startDate the start date of this build
     */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

    /**
     * Gets the ending date of this build.
     * 
     * @return the ending date of this build
     */
	public Date getEndDate() {
		return this.endDate;
	}

    /**
     * Sets the ending date of this build.
     * 
     * @param endDate the ending date of this build
     */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

    /**
     * Gets the version of this build.
     * 
     * @return the version of this build
     */
	public String getVersion() {
		return this.version;
	}

    /**
     * Sets the version of this build.
     * 
     * @param version the version of this build
     */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Checks if this build has a corresponding label in the VCS repository.
	 * 
	 * @return <code>true</code> if this build has a corresponding label in the VCS repository
	 */
	public boolean isHaveLabelOnHead() {
		return this.haveLabelOnHead;
	}

	/**
	 * Sets <code>true</code> or <code>false</code> if this build has a corresponding label in the VCS repository.
	 * 
	 * @param haveLabelOnHead set <code>true</code> if this build has a label in the VCS repository
	 */
	public void setHaveLabelOnHead(boolean haveLabelOnHead) {
		this.haveLabelOnHead = haveLabelOnHead;
	}

    /**
     * Checks if this is a clean build.
     * 
     * @return <code>true</code> if this is a clean build
     */
	public boolean isCleanBuild() {
		return cleanBuild;
	}

    /**
     * Sets whether this is a clean build.
     * 
     * @param cleanBuild set <code>true</code> if this is a clean build
     */
	public void setCleanBuild(boolean cleanBuild) {
		this.cleanBuild = cleanBuild;
	}

    /**
     * Checks if this is a rebuilt build.
     * 
     * @return <code>true</code> if this is a rebuilt build
     */
	public boolean isRebuild() {
		return this.rebuild;
	}

    /**
     * Sets whether this is a rebuilt build.
     * 
     * @param rebuild set <code>true</code> if this is a rebuilt build
     */
	public void setRebuild(boolean rebuild) {
		this.rebuild = rebuild;
	}

    /**
     * Gets the URL of this build.
     * 
     * @return the URL of this build
     */
	public String getUrl() {
		return this.url;
	}

    /**
     * Sets the URL of this build.
     * 
     * @param url the URL of this build
     */
	public void setUrl(String url) {
		this.url = url;
	}

    /**
     * Gets the build log URL of this build.
     * 
     * @return the build log URL of this build
     */
	public String getBuildLogUrl() {
		return this.buildLogUrl;
	}

    /**
     * Sets the build log URL of this build.
     * 
     * @param buildLogUrl the build log URL of this build
     */
	public void setBuildLogUrl(String buildLogUrl) {
		this.buildLogUrl = buildLogUrl;
	}

    /**
     * Gets the revision log URL of this build.
     *
     * @return the revision log URL of this build
     */
    public String getRevisionLogUrl() {
        return this.revisionLogUrl;
    }

    /**
     * Sets the revision log URL of this build.
     *
     * @param revisionLogUrl the revision log URL of this build
     */
    public void setRevisionLogUrl(String revisionLogUrl) {
        this.revisionLogUrl = revisionLogUrl;
    }

    /**
     * Gets the identifier of this build's build schedule.
     * 
     * @return the identifier of the build schedule
     */
	public long getBuildScheduleId() {
		return buildScheduleId;
	}

    /**
     * Sets the identifier of this build's build schedule.
     * 
     * @param buildScheduleId the identifier of the build schedule
     */
	public void setBuildScheduleId(long buildScheduleId) {
		this.buildScheduleId = buildScheduleId;
	}

    /**
     * Gets the label strategy for this build.
     * 
     * @return the label strategy for this build
     * @see BuildScheduleFacade#LABEL_WHEN_SUCCESS
     * @see BuildScheduleFacade#LABEL_NONE
     * @see BuildScheduleFacade#LABEL_ALWAYS
     */
	public String getLabelStrategy() {
		return labelStrategy;
	}

    /**
     * Sets the label strategy for this build.
     * 
     * @param labelStrategy the labelStrategy the label strategy for this build
     * @see BuildScheduleFacade#LABEL_WHEN_SUCCESS
     * @see BuildScheduleFacade#LABEL_NONE
     * @see BuildScheduleFacade#LABEL_ALWAYS
     */
	public void setLabelStrategy(String labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

    /**
     * Gets the post-build strategy for this build.
     * 
     * @return the post-build strategy for this build
     * @see BuildScheduleFacade#POSTBUILD_NONE
     * @see BuildScheduleFacade#POSTBUILD_WHEN_SUCCESS
     * @see BuildScheduleFacade#POSTBUILD_WHEN_FAILED
     * @see BuildScheduleFacade#POSTBUILD_ALWAYS
     */
	public String getPostbuildStrategy() {
		return postbuildStrategy;
	}

    /**
     * Sets the post-build strategy for this build.
     * 
     * @param postbuildStrategy the postbuildStrategy the post-build strategy for this build
     * @see BuildScheduleFacade#POSTBUILD_NONE
     * @see BuildScheduleFacade#POSTBUILD_WHEN_SUCCESS
     * @see BuildScheduleFacade#POSTBUILD_WHEN_FAILED
     * @see BuildScheduleFacade#POSTBUILD_ALWAYS
     */
	public void setPostbuildStrategy(String postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}
}