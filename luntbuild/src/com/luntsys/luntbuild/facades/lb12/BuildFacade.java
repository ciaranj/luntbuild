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

package com.luntsys.luntbuild.facades.lb12;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Build facade.
 *
 * @author robin shine
 * @see com.luntsys.luntbuild.db.Build
 */
public class BuildFacade {
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

	/**
	 * Label strategy of this build
	 */
	private int labelStrategy;

	/**
	 * Post-build strategy of this build
	 */
	private int postbuildStrategy;

	/**
	 * Does this build have corresponding label in the vcs repository for
	 * head revisions configured for this build's vcs setting?
	 */
	private boolean haveLabelOnHead = false;

	/**
	 * Whether or not this is a clean build
	 */
	private int buildType;

	/**
	 * Is this build a rebuild?
	 */
	private boolean rebuild;

	/**
	 * Version control systems used to construct this build
	 */
	private List vcsList = new ArrayList();

	/**
	 * Builders used to construct this build
	 */
	private List builderList = new ArrayList();

	/**
	 * Post-builders used to construct this build
	 */
	private List postbuilderList = new ArrayList();

	/**
	  * Should not mark this as transient. Otherwise, this property will get lost when
	  * transferred back to hessian client
	 */
	private String url;
	private String buildLogUrl;
	private String revisionLogUrl;
    private String systemLogUrl;

	private long scheduleId;

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
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_RUNNING
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_SUCCESS
     */
	public int getStatus() {
		return this.status;
	}

    /**
     * Sets the status of this build.
     * 
     * @param status the status of this build
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_RUNNING
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_SUCCESS
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
     * Gets the build type of this build.
     * 
     * @return the build type of this build
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_CLEAN
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_INCREMENT
     */
	public int getBuildType() {
		return this.buildType;
	}

    /**
     * Sets the build type of this build.
     * 
     * @param buildType the build type the build type of this build
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_CLEAN
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_INCREMENT
     */
	public void setBuildType(int buildType) {
		this.buildType = buildType;
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
     * Gets the system log URL.
     * 
     * @return the system log URL
     * @since 1.3
     */
	public String getSystemLogUrl() {
		return this.systemLogUrl;
	}

    /**
     * Sets the system log URL.
     * 
     * @param systemLogUrl the system log URL
     * @since 1.3
     */
	public void setSystemLogUrl(String systemLogUrl) {
		this.systemLogUrl = systemLogUrl;
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
     * Gets the identifier of this build's schedule.
     * 
     * @return the identifier of the schedule
     */
	public long getScheduleId() {
		return this.scheduleId;
	}

    /**
     * Sets the identifier of this build's schedule.
     * 
     * @param scheduleId the identifier of the schedule
     */
	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}

    /**
     * Gets the label strategy for this build.
     * 
     * @return the label strategy for this build
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_IF_SUCCESS
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_NONE
     */
	public int getLabelStrategy() {
		return this.labelStrategy;
	}

    /**
     * Sets the label strategy for this build.
     * 
     * @param labelStrategy the labelStrategy the label strategy for this build
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_IF_SUCCESS
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_NONE
     */
	public void setLabelStrategy(int labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

    /**
     * Gets the post-build strategy for this build.
     * 
     * @return the post-build strategy for this build
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_NONE
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_SUCCESS
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_ALWAYS
     */
	public int getPostbuildStrategy() {
		return this.postbuildStrategy;
	}

    /**
     * Sets the post-build strategy for this build.
     * 
     * @param postbuildStrategy the postbuildStrategy the post-build strategy for this build
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_NONE
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_SUCCESS
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_ALWAYS
     */
	public void setPostbuildStrategy(int postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

    /**
     * Gets the VCS list of this build.
     * 
     * @return the VCS list of this build
	 * @see com.luntsys.luntbuild.vcs.Vcs
     */
	public List getVcsList() {
		return this.vcsList;
	}

    /**
     * Sets the VCS list of this build.
     * 
     * @param vcsList the list of VCS adaptors
	 * @see com.luntsys.luntbuild.vcs.Vcs
     */
	public void setVcsList(List vcsList) {
		this.vcsList = vcsList;
	}

    /**
     * Gets the builder list of this build.
     * 
     * @return the builder list of this build
	 * @see com.luntsys.luntbuild.builders.Builder
     */
	public List getBuilderList() {
		return this.builderList;
	}

    /**
     * Sets the builder list of this build.
     * 
     * @param builderList the list of builders
	 * @see com.luntsys.luntbuild.builders.Builder
     */
	public void setBuilderList(List builderList) {
		this.builderList = builderList;
	}

    /**
     * Gets the post-builder list of this build.
     * 
     * @return the post-builder list of this build
	 * @see com.luntsys.luntbuild.builders.Builder
     */
	public List getPostbuilderList() {
		return this.postbuilderList;
	}

    /**
     * Sets the post-builder list of this build.
     * 
     * @param postbuilderList the list of post-builders
	 * @see com.luntsys.luntbuild.builders.Builder
     */
	public void setPostbuilderList(List postbuilderList) {
		this.postbuilderList = postbuilderList;
	}
}
