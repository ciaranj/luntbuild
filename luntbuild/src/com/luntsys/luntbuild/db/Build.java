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

import java.io.File;
import java.util.Date;
import java.util.Map;

import com.luntsys.luntbuild.remoting.BuildFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.vcs.Vcs;
import com.luntsys.luntbuild.web.Home;

/**
 * This class mapps to a hibernate entity. It represents a
 * execution result of a particular build schedule
 *
 * @author robin shine
 */
public class Build {
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
	/**
	 * Label strategy of this build
	 */
	private String labelStrategy;
	/**
	 * Post-build strategy of this build
	 */
	private String postbuildStrategy;

	/**
	 * Does this build have corresponding label in the vcs repository for
	 * head revisions configured for this build's vcs setting?
	 */
	private boolean haveLabelOnHead = false;

	/**
	 * Whether or not this is a clean build
	 */
	private boolean cleanBuild;

	/**
	 * Is this build a rebuild?
	 */
	private boolean rebuild;

	private BuildSchedule buildSchedule;

	/**
	 * The version control system object attached to current build
	 */
	private Vcs vcs;

	public boolean isCleanBuild() {
		return cleanBuild;
	}

	public void setCleanBuild(boolean cleanBuild) {
		this.cleanBuild = cleanBuild;
	}

	public String getLabelStrategy() {
		return labelStrategy;
	}

	public void setLabelStrategy(String labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

	public boolean isHaveLabelOnHead() {
		return haveLabelOnHead;
	}

	public void setHaveLabelOnHead(boolean haveLabelOnHead) {
		this.haveLabelOnHead = haveLabelOnHead;
	}

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

	public Vcs getVcs() {
		return vcs;
	}

	public void setVcs(Vcs vcs) {
		this.vcs = vcs;
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

	public void validate(){
		// current does nothing
	}

	/**
	 * Return url of this build
	 * @param properties luntbuild system level properties
	 * @return
	 */
	public String getUrl(Map properties) {
		return Luntbuild.getServletUrl(properties) + "?service=external/Home&sp=l" +
				Home.SERVICE_PARAMETER_BUILD + "&sp=l" + getId();
	}

	/**
	 * Return the build log url of this build
	 * @param properties luntbuild system level properties
	 * @return
	 */
	public String getBuildLogUrl(Map properties) {
		return Luntbuild.getServletUrl(properties) + "?service=external/Home&sp=l" +
				Home.SERVICE_PARAMETER_BUILDLOG + "&sp=l" + getId();
	}

	/**
	 * Return revision log url of this build
	 * @param properties luntbuild system level properties
	 * @return
	 */
	public String getRevisionLogUrl(Map properties) {
		return Luntbuild.getServletUrl(properties) + "?service=external/Home&sp=l" +
				Home.SERVICE_PARAMETER_REVISIONLOG + "&sp=l" + getId();
	}

	public BuildFacade getFacade(Map properties) {
		BuildFacade facade = new BuildFacade();
		facade.setId(getId());
		facade.setCleanBuild(isCleanBuild());
		facade.setHaveLabelOnHead(isHaveLabelOnHead());
		facade.setRebuild(isRebuild());
		facade.setStatus(getStatus());
		facade.setVersion(getVersion());
		facade.setStartDate(getStartDate());
		facade.setEndDate(getEndDate());
		facade.setUrl(getUrl(properties));
		facade.setBuildLogUrl(getBuildLogUrl(properties));
		facade.setRevisionLogUrl(getRevisionLogUrl(properties));
		return facade;
	}

	public void setFacade(BuildFacade facade) {
		validate();
	}

	/**
	 * Composite the publish directory for current build. Publish directory is used to hold
	 * output of this build, including build log and build artifacts, etc.
	 *
	 * @param properties luntbuild system level properties
	 * @return
	 */
	public String getPublishDir(Map properties) {
		String publishDir = (String) properties.get("publishDir");
		if (Luntbuild.isEmpty(publishDir))
			publishDir = new File(Luntbuild.installDir + "/publish").getAbsolutePath();
		return publishDir + File.separator + getId();
	}

	public String getPostbuildStrategy() {
		return postbuildStrategy;
	}

	public void setPostbuildStrategy(String postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}
}
