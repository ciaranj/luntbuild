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
 * Facade of a build in luntbuild system. Mainly used by web service
 * client to operate on properties of a build object.
 *
 * @author robin shine
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

	private long scheduleId;

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
	 * @return one value of {@link com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_FAILED},
	 * {@link com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_SUCCESS},
	 * {@link com.luntsys.luntbuild.facades.Constants#BUILD_STATUS_RUNNING}
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set status of this build
	 * @param status refer to return value of {@link #getStatus()}
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

	public int getBuildType() {
		return buildType;
	}

	public void setBuildType(int buildType) {
		this.buildType = buildType;
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

	public long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public int getLabelStrategy() {
		return labelStrategy;
	}

	public void setLabelStrategy(int labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

	public int getPostbuildStrategy() {
		return postbuildStrategy;
	}

	public void setPostbuildStrategy(int postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

	public List getVcsList() {
		return vcsList;
	}

	public void setVcsList(List vcsList) {
		this.vcsList = vcsList;
	}

	public List getBuilderList() {
		return builderList;
	}

	public void setBuilderList(List builderList) {
		this.builderList = builderList;
	}

	public List getPostbuilderList() {
		return postbuilderList;
	}

	public void setPostbuilderList(List postbuilderList) {
		this.postbuilderList = postbuilderList;
	}
}