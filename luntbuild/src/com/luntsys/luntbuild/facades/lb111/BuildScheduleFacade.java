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
 * Facade of a luntbuild build schedule. Mainly used by web service client to operate
 * on properties of a build schedule
 */
public class BuildScheduleFacade {
	public static final int UNKNOWN = 0;
	public static final int SUCCESS = 1;
	public static final int FAILED =2;
	public static final int RUNNING = 3;

	public static final String BUILD_TYPE_CLEAN = "clean";
	public static final String BUILD_TYPE_INCREMENT = "increment";

	public static final String BUILD_WHEN_NECESSARY = "build when necessary";
	public static final String BUILD_ALWAYS_IF_FAILED = "build always if failed";
	public static final String BUILD_ALWAYS = "build always";
	public static final String BUILD_NONE = "do not build";

	public static final String LABEL_WHEN_SUCCESS = "label successful builds";
	public static final String LABEL_NONE = "do not label";
	public static final String LABEL_ALWAYS = "label always";

	public static final String NOTIFY_WHEN_SUCCESS = "notify when build success";
	public static final String NOTIFY_WHEN_FAILED = "notify when build failed";
	public static final String NOTIFY_NONE = "do not notify";
	public static final String NOTIFY_ALWAYS = "notify always";

	public static final String POSTBUILD_NONE = "do not post-build";
	public static final String POSTBUILD_WHEN_SUCCESS = "post-build when success";
	public static final String POSTBUILD_WHEN_FAILED = "post-build when failed";
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
	 * Get id of this build schedule
	 * @return id of this build schedule
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set id of this build schedule
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Wheter or not this build schedule schedules clean build
	 * @return boolean value
	 */
	public boolean isCleanBuild() {
		return cleanBuild;
	}

	/**
	 * Set whether or not this build schedules clean build
	 * @param cleanBuild
	 */
	public void setCleanBuild(boolean cleanBuild) {
		this.cleanBuild = cleanBuild;
	}

	/**
	 * Get build strategy of this build schedule.
	 * @return one of the value of {@link BuildScheduleFacade#BUILD_ALWAYS},
	 * {@link BuildScheduleFacade#BUILD_ALWAYS_IF_FAILED}, {@link BuildScheduleFacade#BUILD_NONE},
	 * {@link BuildScheduleFacade#BUILD_WHEN_NECESSARY}
	 */
	public String getBuildStrategy() {
		return buildStrategy;
	}

	/**
	 * Set build strategy of this build schedule
	 * @param buildStrategy one of the value of {@link BuildScheduleFacade#BUILD_ALWAYS},
	 * {@link BuildScheduleFacade#BUILD_ALWAYS_IF_FAILED}, {@link BuildScheduleFacade#BUILD_NONE},
	 * {@link BuildScheduleFacade#BUILD_WHEN_NECESSARY}
	 */
	public void setBuildStrategy(String buildStrategy) {
		this.buildStrategy = buildStrategy;
	}

	/**
	 * Get label strategy of this build schedule
	 * @return one of the value of {@link BuildScheduleFacade#LABEL_ALWAYS},
	 * {@link BuildScheduleFacade#LABEL_NONE}, {@link BuildScheduleFacade#LABEL_WHEN_SUCCESS}
	 */
	public String getLabelStrategy() {
		return labelStrategy;
	}

	/**
	 * Set label strategy of this build schedule
	 * @param labelStrategy one of the value of {@link BuildScheduleFacade#LABEL_ALWAYS},
	 * {@link BuildScheduleFacade#LABEL_NONE}, {@link BuildScheduleFacade#LABEL_WHEN_SUCCESS}
	 */
	public void setLabelStrategy(String labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

	/**
	 * Get notify strategy of this build schedule
	 * @return one of the value of {@link BuildScheduleFacade#NOTIFY_ALWAYS},
	 * {@link BuildScheduleFacade#NOTIFY_NONE}, {@link BuildScheduleFacade#NOTIFY_WHEN_FAILED},
	 * {@link BuildScheduleFacade#NOTIFY_WHEN_SUCCESS}
	 */
	public String getNotifyStrategy() {
		return notifyStrategy;
	}

	/**
	 * Set notify strategy of this build
	 * @param notifyStrategy
	 */
	public void setNotifyStrategy(String notifyStrategy) {
		this.notifyStrategy = notifyStrategy;
	}

	/**
	 * Get status of this build schedule
	 * @return one of the value of {@link BuildScheduleFacade#FAILED}, {@link BuildScheduleFacade#RUNNING},
	 * {@link BuildScheduleFacade#UNKNOWN}, {@link BuildScheduleFacade#SUCCESS}
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set status of this build schedule
	 * @param status one of the value of {@link BuildScheduleFacade#FAILED}, {@link BuildScheduleFacade#RUNNING},
	 * {@link BuildScheduleFacade#UNKNOWN}, {@link BuildScheduleFacade#SUCCESS}
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Get the date when this build schedule get its current status
	 * @return date when current status occurs
	 */
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * Set date when this build schedule get its current status
	 * @param statusDate
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * Get url of this build schedule
	 * @return url of this build schedule
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Set url of this build schedule
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Get post-build strategy of this build schedule
	 * @return one of the value of {@link BuildScheduleFacade#POSTBUILD_ALWAYS},
	 * {@link BuildScheduleFacade#POSTBUILD_NONE}, {@link BuildScheduleFacade#POSTBUILD_WHEN_FAILED},
	 * {@link BuildScheduleFacade#POSTBUILD_WHEN_SUCCESS}
	 */
	public String getPostbuildStrategy() {
		return postbuildStrategy;
	}

	/**
	 * Set post-build strategy of this build schedule
	 * @param postbuildStrategy one of the value of {@link BuildScheduleFacade#POSTBUILD_ALWAYS},
	 * {@link BuildScheduleFacade#POSTBUILD_NONE}, {@link BuildScheduleFacade#POSTBUILD_WHEN_FAILED},
	 * {@link BuildScheduleFacade#POSTBUILD_WHEN_SUCCESS}
	 */
	public void setPostbuildStrategy(String postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

	public long getViewId() {
		return viewId;
	}

	public void setViewId(long viewId) {
		this.viewId = viewId;
	}

	public long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}
}
