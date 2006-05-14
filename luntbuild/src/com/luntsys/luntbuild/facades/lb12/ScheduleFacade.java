/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-20
 * Time: 21:27:07
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
 * Facade of a luntbuild schedule.. Mainly used by web service client to operate on
 * properties of a schedule
 *
 * @author robin shine
 */
public class ScheduleFacade {
	private long id;
	private String name;
	private String description;
	private String nextVersion;
	private int triggerType;
	private long repeatInterval;
	private String cronExpression;
	private int buildType;
	private String buildNecessaryCondition;
	private List associatedBuilderNames = new ArrayList();
	private List associatedPostbuilderNames = new ArrayList();
	private int triggerDependencyStrategy;
	private int buildCleanupStrategy;
	private String buildCleanupStrategyData;
	private int postbuildStrategy;
	private int labelStrategy;
	private int notifyStrategy;
	private int status;
	private Date statusDate;

	/**
	 * Should not mark this as transient. Otherwise, this property will get lost when
	 * transferred back to hessian client
	 */
	private String url;

	private List dependentScheduleIds = new ArrayList();
	private String workingPath;
	private long projectId;

	/**
	 * Get id of this schedule
	 * @return id of this schedule
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set id of this schedule
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get name of this schedule
	 * @return name of this schedule
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name of this schedule
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get description of this schedule
	 * @return description of this schedule
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set description of this schedule
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get trigger type of this schedule
	 * @return one value of {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_CRON},
	 * {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_MANUAL},
	 * {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_SIMPLE}
	 */
	public int getTriggerType() {
		return triggerType;
	}

	/**
	 * Set trigger type of this schedule
	 * @param triggerType refer to {@link #getTriggerType()}
	 */
	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}

	/**
	 * Get repeat interval of this schedule, only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_SIMPLE}
	 * @return repeat interval of this schedule
	 */
	public long getRepeatInterval() {
		return repeatInterval;
	}

	/**
	 * Set repeat interval of this schedule. Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_SIMPLE}
	 * @param repeatInterval
	 */
	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	/**
	 * Get cron expression of this schedule. Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_CRON}
	 * @return cron expression of this schedule
	 */
	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * Set cron expression of this schedule. Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_CRON}
	 * @param cronExpression
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * Get build type of this schedule.
	 * @return one value of {@link com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_CLEAN},
	 * and {@link com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_INCREMENT}
	 */
	public int getBuildType() {
		return buildType;
	}

	/**
	 * Set build type of this schedule
	 * @param buildType refer to return value of {@link #getBuildType()}
	 */
	public void setBuildType(int buildType) {
		this.buildType = buildType;
	}

	/**
	 * Get label strategy of this schedule
	 * @return one value of {@link com.luntsys.luntbuild.facades.Constants#LABEL_ALWAYS},
	 * {@link com.luntsys.luntbuild.facades.Constants#LABEL_NONE}, and
	 * {@link com.luntsys.luntbuild.facades.Constants#LABEL_IF_SUCCESS}
	 */
	public int getLabelStrategy() {
		return labelStrategy;
	}

	/**
	 * Set label strategy of this schedule
	 * @param labelStrategy refer to return value of {@link #getLabelStrategy()}
	 */
	public void setLabelStrategy(int labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

	/**
	 * Get notify strategy of this schedule
	 * @return one value of {@link com.luntsys.luntbuild.facades.Constants#NOTIFY_ALWAYS},
	 * {@link com.luntsys.luntbuild.facades.Constants#NOTIFY_NONE},
	 * {@link com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_FAILED},
	 * {@link com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_SUCCESS}, and
	 * {@link com.luntsys.luntbuild.facades.Constants#NOTIFY_WHEN_STATUS_CHANGED}
	 */
	public int getNotifyStrategy() {
		return notifyStrategy;
	}

	/**
	 * Set notify strategy of this schedule
	 * @param notifyStrategy refer to return value of {@link #getNotifyStrategy()}
	 */
	public void setNotifyStrategy(int notifyStrategy) {
		this.notifyStrategy = notifyStrategy;
	}

	/**
	 * Get status of this schedule
	 * @return one value of {@link com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_FAILED},
	 * {@link com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_RUNNING}, and
	 * {@link com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_SUCCESS}
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set status of this schedule
	 * @param status refer to return value of {@link #getStatus()}
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Get the date when this schedule get its current status
	 * @return date when current status occurs
	 */
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * Set date when this schedule get its current status
	 * @param statusDate
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * Get url of this schedule
	 * @return url of this schedule
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Set url of this schedule
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Get post-build strategy of this schedule
	 * @return one of the value of {@link com.luntsys.luntbuild.facades.Constants#POSTBUILD_ALWAYS},
	 * {@link com.luntsys.luntbuild.facades.Constants#POSTBUILD_NONE},
	 * {@link com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_FAILED},
	 * {@link com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_SUCCESS}
	 */
	public int getPostbuildStrategy() {
		return postbuildStrategy;
	}

	/**
	 * Set post-build strategy of this schedule
	 * @param postbuildStrategy refer to return value of {@link #getPostbuildStrategy()}
	 */
	public void setPostbuildStrategy(int postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

	/**
	 * Get build necessary condition for this schedule
	 * @return build necessary condition
	 */
	public String getBuildNecessaryCondition() {
		return buildNecessaryCondition;
	}

	/**
	 * Set build necessary condition for this schedule
	 * @param buildNecessaryCondition
	 */
	public void setBuildNecessaryCondition(String buildNecessaryCondition) {
		this.buildNecessaryCondition = buildNecessaryCondition;
	}

	/**
	 * Get dependent triggering strategy, should be one value of
	 * {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_ALL_DEPENDENT_SCHEDULES},
	 * {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_NONE_DEPENDENT_SCHEDULES},
	 * {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_DEPENDS_ON_THIS},
	 * {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_THIS_DEPENDS_ON}
	 */
	public int getTriggerDependencyStrategy() {
		return triggerDependencyStrategy;
	}

	/**
	 * Set dependent triggering strategy
	 * @param triggerDependencyStrategy see reteurn value of {@link this#getTriggerDependencyStrategy()}
	 */
	public void setTriggerDependencyStrategy(int triggerDependencyStrategy) {
		this.triggerDependencyStrategy = triggerDependencyStrategy;
	}

	/**
	 * Get build cleanup strategy
	 * @return one value of {@link com.luntsys.luntbuild.facades.Constants#BUILD_CLEANUP_NONE},
	 * {@link com.luntsys.luntbuild.facades.Constants#BUILD_KEEP_BY_COUNT},
	 * {@link com.luntsys.luntbuild.facades.Constants#BUILD_KEEP_BY_DAYS}
	 */
	public int getBuildCleanupStrategy() {
		return buildCleanupStrategy;
	}

	/**
	 * Set build cleanup strategy
	 * @param buildCleanupStrategy see return value of {@link this#getBuildCleanupStrategy()}
	 */
	public void setBuildCleanupStrategy(int buildCleanupStrategy) {
		this.buildCleanupStrategy = buildCleanupStrategy;
	}

	/**
	 * Get build cleanup data
	 * @return
	 */
	public String getBuildCleanupStrategyData() {
		return buildCleanupStrategyData;
	}

	/**
	 * Set build cleanup data
	 * @param buildCleanupStrategyData
	 */
	public void setBuildCleanupStrategyData(String buildCleanupStrategyData) {
		this.buildCleanupStrategyData = buildCleanupStrategyData;
	}

	/**
	 * Get next version for this schedule
	 * @return next version for this schedule
	 */
	public String getNextVersion() {
		return nextVersion;
	}

	/**
	 * Set next version for this schedule
	 * @param nextVersion
	 */
	public void setNextVersion(String nextVersion) {
		this.nextVersion = nextVersion;
	}

	/**
	 * Get names of associated builders for current schedule
	 * @return names of associated builders for current schedule
	 */
	public List getAssociatedBuilderNames() {
		return associatedBuilderNames;
	}

	/**
	 * Set names of associated builders for current schedule
	 * @param associatedBuilderNames
	 */
	public void setAssociatedBuilderNames(List associatedBuilderNames) {
		this.associatedBuilderNames = associatedBuilderNames;
	}

	/**
	 * Get names of associated post-builders for current schedule
	 * @return names of associated post-builders for current schedule
	 */
	public List getAssociatedPostbuilderNames() {
		return associatedPostbuilderNames;
	}

	/**
	 * Set names of associated post-builders for current schedule
	 * @param associatedPostbuilderNames
	 */
	public void setAssociatedPostbuilderNames(List associatedPostbuilderNames) {
		this.associatedPostbuilderNames = associatedPostbuilderNames;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public List getDependentScheduleIds() {
		return dependentScheduleIds;
	}

	public void setDependentScheduleIds(List dependentScheduleIds) {
		this.dependentScheduleIds = dependentScheduleIds;
	}

	public String getWorkingPath() {
		return workingPath;
	}

	public void setWorkingPath(String workingPath) {
		this.workingPath = workingPath;
	}
}
