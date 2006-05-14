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
 * Facade of a build in luntbuild system. Mainly used by web service
 * client to operate on properties of a build object.
 *
 * @author robin shine
 */
public class BuildFacade {
	/**
	 * The build status is unknown
	 */
	public static final int UNKNOWN = 0;
	/**
	 * The build status is successful
	 */
	public static final int SUCCESS = 1;
	/**
	 * The build status is failed
	 */
	public static final int FAILED = 2;
	/**
	 * The build status is running
	 */
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
	 * Get id of this build.
	 * @return id of this build
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set id of this build
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get status of this build.
	 * @return one value of {@link BuildFacade#UNKNOWN}, {@link BuildFacade#SUCCESS},
	 * {@link BuildFacade#FAILED}, or {@link BuildFacade#RUNNING}
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set status of this build
	 * @param status one value of {@link BuildFacade#UNKNOWN}, {@link BuildFacade#SUCCESS},
	 * {@link BuildFacade#FAILED}, or {@link BuildFacade#RUNNING}
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Get start date of this build
	 * @return start date of this build
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Set start date of this build
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get end date of this build
	 * @return end date of this build
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Set end date of this build
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Get version of this build
	 * @return version of this build
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Set version of this build
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Whether or not head revisions of this build has been labeled
	 * in the vcs repository
	 * @return boolean value
	 */
	public boolean isHaveLabelOnHead() {
		return haveLabelOnHead;
	}

	/**
	 * Set the value indicates whether or not head revisions of this build has been labeled
	 * in the vcs repository
	 * @param haveLabelOnHead
	 */
	public void setHaveLabelOnHead(boolean haveLabelOnHead) {
		this.haveLabelOnHead = haveLabelOnHead;
	}

	/**
	 * Wheter or not this is a clean build
	 * @return boolean value
	 */
	public boolean isCleanBuild() {
		return cleanBuild;
	}

	/**
	 * Set whether or not this is a clean build
	 * @param cleanBuild
	 */
	public void setCleanBuild(boolean cleanBuild) {
		this.cleanBuild = cleanBuild;
	}

	/**
	 * Whether or not this is a rebuild
	 * @return boolean value
	 */
	public boolean isRebuild() {
		return rebuild;
	}

	/**
	 * Set whether or not this is a rebuild
	 * @param rebuild
	 */
	public void setRebuild(boolean rebuild) {
		this.rebuild = rebuild;
	}

	/**
	 * Get url of this build
	 * @return url of this build
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Set url of this build
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Get url of this build's buid log
	 * @return url of this build's build log
	 */
	public String getBuildLogUrl() {
		return buildLogUrl;
	}

	/**
	 * Set url of this build's build log
	 * @param buildLogUrl
	 */
	public void setBuildLogUrl(String buildLogUrl) {
		this.buildLogUrl = buildLogUrl;
	}

	/**
	 * Get url of this build's revision log
	 * @return url of this build's revision log
	 */
	public String getRevisionLogUrl() {
		return revisionLogUrl;
	}

	/**
	 * Set url of this build's revision log
	 * @param revisionLogUrl
	 */
	public void setRevisionLogUrl(String revisionLogUrl) {
		this.revisionLogUrl = revisionLogUrl;
	}

	public long getBuildScheduleId() {
		return buildScheduleId;
	}

	public void setBuildScheduleId(long buildScheduleId) {
		this.buildScheduleId = buildScheduleId;
	}

	public String getLabelStrategy() {
		return labelStrategy;
	}

	public void setLabelStrategy(String labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

	public String getPostbuildStrategy() {
		return postbuildStrategy;
	}

	public void setPostbuildStrategy(String postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}
}