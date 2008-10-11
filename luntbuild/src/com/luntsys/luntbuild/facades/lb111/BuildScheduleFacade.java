/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 8:35:23
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
 * Build schedule facade.
 */
public class BuildScheduleFacade {
	/** Build schedule status, running */
	public static final int UNKNOWN = 0;
	/** Build schedule status, success */
	public static final int SUCCESS = 1;
	/** Build schedule status, failed */
	public static final int FAILED =2;
	/** Build schedule status, running */
	public static final int RUNNING = 3;

	/** Build type, clean */
	public static final String BUILD_TYPE_CLEAN = "clean";
	/** Build type, increment */
	public static final String BUILD_TYPE_INCREMENT = "increment";

	/** Build strategy, build when necessary */
	public static final String BUILD_WHEN_NECESSARY = "build when necessary";
	/** Build strategy, build always if failed */
	public static final String BUILD_ALWAYS_IF_FAILED = "build always if failed";
	/** Build strategy, build always */
	public static final String BUILD_ALWAYS = "build always";
	/** Build strategy, do not build */
	public static final String BUILD_NONE = "do not build";

	/** Label strategy, label successful builds */
	public static final String LABEL_WHEN_SUCCESS = "label successful builds";
	/** Label strategy, do not label */
	public static final String LABEL_NONE = "do not label";
	/** Label strategy, label always */
	public static final String LABEL_ALWAYS = "label always";

	/** Notify strategy, notify when build success */
	public static final String NOTIFY_WHEN_SUCCESS = "notify when build success";
	/** Notify strategy, notify when build failed */
	public static final String NOTIFY_WHEN_FAILED = "notify when build failed";
	/** Notify strategy, do not notify */
	public static final String NOTIFY_NONE = "do not notify";
	/** Notify strategy, notify always */
	public static final String NOTIFY_ALWAYS = "notify always";

	/** Post-build strategy, do not post-build */
	public static final String POSTBUILD_NONE = "do not post-build";
	/** Post-build strategy, post-build when success */
	public static final String POSTBUILD_WHEN_SUCCESS = "post-build when success";
	/** Post-build strategy, post-build when failed */
	public static final String POSTBUILD_WHEN_FAILED = "post-build when failed";
	/** Post-build strategy, post-build always */
	public static final String POSTBUILD_ALWAYS = "post-build always";

	private long id;
	private boolean cleanBuild;
	private String buildStrategy;
	private String postbuildStrategy;
	private String labelStrategy;
	private String notifyStrategy;
	private int status;
	private Date statusDate;
	private transient String url;
	private long viewId;
	private long scheduleId;

    /**
     * Gets the indentifier of this build schedule.
     * 
     * @return the indentifier of this build schedule
     */
	public long getId() {
		return id;
	}

    /**
     * Sets the indentifier of this build schedule.
     * 
     * @param id the indentifier of this build schedule
     */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Checks if this build schedule schedules a clean build.
	 * 
	 * @return <code>true</code> if this build schedule schedules a clean build
	 */
	public boolean isCleanBuild() {
		return cleanBuild;
	}

	/**
	 * Sets whether this build schedule schedules a clean build.
	 * 
	 * @param cleanBuild set <code>true</code> if this build schedule schedules a clean build
	 */
	public void setCleanBuild(boolean cleanBuild) {
		this.cleanBuild = cleanBuild;
	}

    /**
     * Gets the build strategy of this build schedule.
     * 
     * @return the build strategy
     * @see BuildScheduleFacade#BUILD_ALWAYS
     * @see BuildScheduleFacade#BUILD_ALWAYS_IF_FAILED
     * @see BuildScheduleFacade#BUILD_NONE
     * @see BuildScheduleFacade#BUILD_WHEN_NECESSARY
     */
	public String getBuildStrategy() {
		return buildStrategy;
	}

    /**
     * Sets the build strategy of this build schedule.
     * 
     * @param buildStrategy the build strategy
     * @see BuildScheduleFacade#BUILD_ALWAYS
     * @see BuildScheduleFacade#BUILD_ALWAYS_IF_FAILED
     * @see BuildScheduleFacade#BUILD_NONE
     * @see BuildScheduleFacade#BUILD_WHEN_NECESSARY
     */
	public void setBuildStrategy(String buildStrategy) {
		this.buildStrategy = buildStrategy;
	}

    /**
     * Gets the label strategy of this build schedule.
     * 
     * @return the label strategy
     * @see BuildScheduleFacade#LABEL_ALWAYS
     * @see BuildScheduleFacade#LABEL_NONE
     * @see BuildScheduleFacade#LABEL_WHEN_SUCCESS
     */
	public String getLabelStrategy() {
		return labelStrategy;
	}

    /**
     * Sets the label strategy of this build schedule.
     * 
     * @param labelStrategy the label strategy
     * @see BuildScheduleFacade#LABEL_ALWAYS
     * @see BuildScheduleFacade#LABEL_NONE
     * @see BuildScheduleFacade#LABEL_WHEN_SUCCESS
     */
	public void setLabelStrategy(String labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

    /**
     * Gets the notify strategy of this build schedule.
     * 
     * @return the notify strategy
     * @see BuildScheduleFacade#NOTIFY_ALWAYS
     * @see BuildScheduleFacade#NOTIFY_NONE
     * @see BuildScheduleFacade#NOTIFY_WHEN_FAILED
     * @see BuildScheduleFacade#NOTIFY_WHEN_SUCCESS
     */
	public String getNotifyStrategy() {
		return notifyStrategy;
	}

    /**
     * Sets the notify strategy of this build schedule.
     * 
     * @param notifyStrategy the notify strategy
     * @see BuildScheduleFacade#NOTIFY_ALWAYS
     * @see BuildScheduleFacade#NOTIFY_NONE
     * @see BuildScheduleFacade#NOTIFY_WHEN_FAILED
     * @see BuildScheduleFacade#NOTIFY_WHEN_SUCCESS
     */
	public void setNotifyStrategy(String notifyStrategy) {
		this.notifyStrategy = notifyStrategy;
	}

    /**
     * Gets the status of this build schedule.
     * 
     * @return the status
     * @see BuildScheduleFacade#UNKNOWN
     * @see BuildScheduleFacade#SUCCESS
     * @see BuildScheduleFacade#FAILED
     * @see BuildScheduleFacade#RUNNING
     */
	public int getStatus() {
		return status;
	}

    /**
     * Sets the status of this build schedule.
     * 
     * @param status the status
     * @see BuildScheduleFacade#UNKNOWN
     * @see BuildScheduleFacade#SUCCESS
     * @see BuildScheduleFacade#FAILED
     * @see BuildScheduleFacade#RUNNING
     */
	public void setStatus(int status) {
		this.status = status;
	}

    /**
     * Gets the date when the status of this build schedule was last updated.
     * 
     * @return the date
     */
	public Date getStatusDate() {
		return statusDate;
	}

    /**
     * Sets the date when the status of this build schedule was last updated.
     * 
     * @param statusDate the date
     */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

    /**
     * Gets the URL to this build schedule.
     * 
     * @return the URL
     */
	public String getUrl() {
		return url;
	}

    /**
     * Sets the URL to this build schedule.
     * 
     * @param url the URL
     */
	public void setUrl(String url) {
		this.url = url;
	}

    /**
     * Gets the post-build strategy of this build schedule.
     * 
     * @return the post-build strategy
     * @see BuildScheduleFacade#POSTBUILD_ALWAYS
     * @see BuildScheduleFacade#POSTBUILD_NONE
     * @see BuildScheduleFacade#POSTBUILD_WHEN_FAILED
     * @see BuildScheduleFacade#POSTBUILD_WHEN_SUCCESS
     */
	public String getPostbuildStrategy() {
		return postbuildStrategy;
	}

    /**
     * Sets the post-build strategy of this build schedule.
     * 
     * @param postbuildStrategy the post-build strategy
     * @see BuildScheduleFacade#POSTBUILD_ALWAYS
     * @see BuildScheduleFacade#POSTBUILD_NONE
     * @see BuildScheduleFacade#POSTBUILD_WHEN_FAILED
     * @see BuildScheduleFacade#POSTBUILD_WHEN_SUCCESS
     */
	public void setPostbuildStrategy(String postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

    /**
     * Gets the identifier of the view of this build schedule.
     * 
     * @return the identifier of the view
     */
	public long getViewId() {
		return viewId;
	}

    /**
     * Sets the identifier of the view of this build schedule.
     * 
     * @param viewId the identifier of the view
     */
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}

    /**
     * Gets the identifier of the schedule of this build schedule.
     * 
     * @return the identifier of the schedule
     */
	public long getScheduleId() {
		return scheduleId;
	}

    /**
     * Sets the identifier of the schedule of this build schedule.
     * 
     * @param scheduleId the identifier of the schedule
     */
	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}
}
