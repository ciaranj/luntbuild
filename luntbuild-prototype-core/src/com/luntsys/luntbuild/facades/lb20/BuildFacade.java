package com.luntsys.luntbuild.facades.lb20;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private String systemLogUrl;

	private long scheduleId;

	/**
	 * Get id of this build.
	 * @return id of this build
	 */
	public long getId() {
		return this.id;
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
		return this.status;
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
		return this.startDate;
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
		return this.endDate;
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
		return this.version;
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
		return this.haveLabelOnHead;
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
	 * @return build type
	 */
	public int getBuildType() {
		return this.buildType;
	}

	/**
	 * @param buildType
	 */
	public void setBuildType(int buildType) {
		this.buildType = buildType;
	}

	/**
	 * Whether or not this is a rebuild
	 * @return boolean value
	 */
	public boolean isRebuild() {
		return this.rebuild;
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
		return this.url;
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
		return this.buildLogUrl;
	}

	/**
	 * Set url of this build's build log
	 * @param buildLogUrl
	 */
	public void setBuildLogUrl(String buildLogUrl) {
		this.buildLogUrl = buildLogUrl;
	}

	/**
	 * Get url of system log
	 * @return url of system log
     * @since 1.3
	 */
	public String getSystemLogUrl() {
		return this.systemLogUrl;
	}

	/**
	 * Set url of system log
	 * @param systemLogUrl
     * @since 1.3
	 */
	public void setSystemLogUrl(String systemLogUrl) {
		this.systemLogUrl = systemLogUrl;
	}

    /**
     * Get url of this build's revision log
     * @return url of this build's revision log
     */
    public String getRevisionLogUrl() {
        return this.revisionLogUrl;
    }

    /**
     * Set url of this build's revision log
     * @param revisionLogUrl
     */
    public void setRevisionLogUrl(String revisionLogUrl) {
        this.revisionLogUrl = revisionLogUrl;
    }

	/**
	 * @return schedule id
	 */
	public long getScheduleId() {
		return this.scheduleId;
	}

	/**
	 * @param scheduleId
	 */
	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}

	/**
	 * @return label strategy
	 */
	public int getLabelStrategy() {
		return this.labelStrategy;
	}

	/**
	 * @param labelStrategy
	 */
	public void setLabelStrategy(int labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

	/**
	 * @return postbuild strategy
	 */
	public int getPostbuildStrategy() {
		return this.postbuildStrategy;
	}

	/**
	 * @param postbuildStrategy
	 */
	public void setPostbuildStrategy(int postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

	/**
	 * @return vcs list
	 */
	public List getVcsList() {
		return this.vcsList;
	}

	/**
	 * @param vcsList
	 */
	public void setVcsList(List vcsList) {
		this.vcsList = vcsList;
	}

	/**
	 * @return builder list
	 */
	public List getBuilderList() {
		return this.builderList;
	}

	/**
	 * @param builderList
	 */
	public void setBuilderList(List builderList) {
		this.builderList = builderList;
	}

	/**
	 * @return postbuilder list
	 */
	public List getPostbuilderList() {
		return this.postbuilderList;
	}

	/**
	 * @param postbuilderList
	 */
	public void setPostbuilderList(List postbuilderList) {
		this.postbuilderList = postbuilderList;
	}
}