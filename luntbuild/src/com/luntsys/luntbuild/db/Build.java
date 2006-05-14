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

import com.luntsys.luntbuild.remoting.Constants;
import com.luntsys.luntbuild.remoting.facade.BuildFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.Home;
import com.luntsys.luntbuild.builders.Builder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * This class mapps to a hibernate entity. It represents a
 * execution result of a particular schedule
 *
 * @author robin shine
 */
public class Build {
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

	private Schedule schedule;

	/**
	 * Version control systems used to construct this build
	 */
	private List vcsList = new ArrayList();

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
		if (obj != null && obj instanceof Build) {
			if (getId() == ((Build) obj).getId())
				return true;
		}
		return false;
	}

	public int hashCode() {
		return (int) getId();
	}

	public void validate() {
		// current does nothing
	}

	/**
	 * Return url of this build
	 *
	 * @param properties luntbuild system level properties
	 * @return
	 */
	public String getUrl(Map properties) {
		return Luntbuild.getServletUrl(properties) + "?service=external/Home&sp=l" +
				Home.SERVICE_PARAMETER_BUILD + "&sp=l" + getId();
	}

	/**
	 * Return the build log url of this build
	 *
	 * @param properties luntbuild system level properties
	 * @return
	 */
	public String getBuildLogUrl(Map properties) {
		return Luntbuild.getServletUrl(properties) + "?service=external/Home&sp=l" +
				Home.SERVICE_PARAMETER_BUILDLOG + "&sp=l" + getId();
	}

	/**
	 * Return revision log url of this build
	 *
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
		facade.setBuildType(getBuildType());
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
			publishDir = new File(Luntbuild.installDir + File.separator + "publish").getAbsolutePath();
		return publishDir + File.separator + getSchedule().getProject().getName() + File.separator +
				getSchedule().getName() + File.separator + getVersion();
	}

	public String getArtifactsDir() throws IOException {
		Map properties = Luntbuild.getDao().loadProperties();
		String artifactsDir =  new File(getPublishDir(properties) + File.separator + Builder.ARTIFACTS_DIR).getCanonicalPath();
		return artifactsDir.replaceAll("\\\\", "\\\\\\\\"); // in order to keep back slash for ognl expression evaluation
	}

	public String getPublishDir() throws IOException {
		String publishDir = new File(getPublishDir(Luntbuild.getDao().loadProperties())).getCanonicalPath();
		return publishDir.replaceAll("\\\\", "\\\\\\\\"); // in order to keep back slash for ognl expression evaluation
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

	public int getBuildType() {
		return buildType;
	}

	public void setBuildType(int buildType) {
		this.buildType = buildType;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public List getVcsList() {
		return vcsList;
	}

	public void setVcsList(List vcsList) {
		this.vcsList = vcsList;
	}

	public boolean isCleanBuild() {
		if (buildType == Constants.BUILD_TYPE_CLEAN)
			return true;
		else
			return false;
	}
}
