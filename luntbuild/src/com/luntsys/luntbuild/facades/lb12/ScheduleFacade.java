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
 * Schedule facade.
 *
 * @author robin shine
 * @see com.luntsys.luntbuild.db.Schedule
 */
public class ScheduleFacade {
	private long id;
	private boolean scheduleDisabled;
	private String name;
	private String description;
	private String nextVersion;
    private String variables;
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
     * Gets the indentifier of this schedule.
     * 
     * @return the indentifier of this schedule
     */
	public long getId() {
		return id;
	}

    /**
     * Sets the indentifier of this schedule.
     * 
     * @param id the indentifier of this schedule
     */
	public void setId(long id) {
		this.id = id;
	}

    /**
     * Checks if this schedule is disabled.
     * 
     * @return <code>true</code> if disabled
     */
	public boolean isScheduleDisabled() {
		return scheduleDisabled;
	}

    /**
     * Sets the disabled state of this schedule.
     * 
     * @param scheduleDisabled set <code>true</code> if schedule is disabled
     */
	public void setScheduleDisabled(boolean scheduleDisabled) {
		this.scheduleDisabled = scheduleDisabled;
	}

    /**
     * Gets the name of this schedule.
     * 
     * @return the name
     */
	public String getName() {
		return name;
	}

    /**
     * Sets the name of this schedule.
     *
     * @param name the name
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Gets the description of this schedule.
     * 
     * @return the description
     */
	public String getDescription() {
		return description;
	}

    /**
     * Sets the description of this schedule.
     * 
     * @param description the description
     */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the trigger type of this schedule.
	 * 
	 * @return the trigger type
	 * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_CRON
	 * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_MANUAL
	 * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_SIMPLE
	 */
	public int getTriggerType() {
		return triggerType;
	}

	/**
	 * Sets the trigger type of this schedule.
	 * 
	 * @param triggerType the trigger type
	 * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_CRON
	 * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_MANUAL
	 * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_SIMPLE
	 */
	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}

	/**
	 * Gets the repeat interval of this schedule.
	 * Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_SIMPLE}.
	 * 
	 * @return the repeat interval of this schedule
	 */
	public long getRepeatInterval() {
		return repeatInterval;
	}

	/**
	 * Sets the repeat interval of this schedule.
	 * Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_SIMPLE}.
	 * 
	 * @param repeatInterval the repeat interval of this schedule
	 */
	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	/**
	 * Gets the cron expression of this schedule.
	 * Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_CRON}.
	 * 
	 * @return the cron expression of this schedule
	 */
	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * Sets the cron expression of this schedule.
	 * Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link com.luntsys.luntbuild.facades.Constants#TRIGGER_TYPE_CRON}.
	 * 
	 * @param cronExpression the cron expression of this schedule
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

    /**
     * Gets the build type of this schedule.
     * 
     * @return the build type
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_CLEAN
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_INCREMENT
     */
	public int getBuildType() {
		return buildType;
	}

    /**
     * Sets the build type of this schedule.
     * 
     * @param buildType the build type
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_CLEAN
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_INCREMENT
     */
	public void setBuildType(int buildType) {
		this.buildType = buildType;
	}

    /**
     * Gets the label strategy of this schedule.
     * 
     * @return the label strategy
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_NONE
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_IF_SUCCESS
     */
	public int getLabelStrategy() {
		return labelStrategy;
	}

    /**
     * Sets the label strategy of this schedule.
     * 
     * @param labelStrategy the label strategy
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_NONE
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_IF_SUCCESS
     */
	public void setLabelStrategy(int labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

    /**
     * Gets the notify strategy of this schedule.
     * 
     * @return the notify strategy
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_NONE
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_SUCCESS
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_WHEN_STATUS_CHANGED
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_FAILED_OR_CHANGED
     */
	public int getNotifyStrategy() {
		return notifyStrategy;
	}

    /**
     * Sets the notify strategy of this schedule.
     * 
     * @param notifyStrategy the notify strategy
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_NONE
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_SUCCESS
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_WHEN_STATUS_CHANGED
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_FAILED_OR_CHANGED
     */
	public void setNotifyStrategy(int notifyStrategy) {
		this.notifyStrategy = notifyStrategy;
	}

    /**
     * Gets the status of this schedule.
     * 
     * @return the status
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_RUNNING
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_SUCCESS
     */
	public int getStatus() {
		return status;
	}

    /**
     * Sets the status of this schedule.
     * 
     * @param status the status
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_RUNNING
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_SUCCESS
     */
	public void setStatus(int status) {
		this.status = status;
	}

    /**
     * Gets the date when the status of this schedule was last updated.
     * 
     * @return the date
     */
	public Date getStatusDate() {
		return statusDate;
	}

    /**
     * Sets the date when the status of this schedule was last updated.
     * 
     * @param statusDate the date
     */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

    /**
     * Gets the URL to this schedule.
     * 
     * @return the URL
     */
	public String getUrl() {
		return url;
	}

    /**
     * Sets the URL to this schedule.
     * 
     * @param url the URL
     */
	public void setUrl(String url) {
		this.url = url;
	}

    /**
     * Gets the post-build strategy of this schedule.
     * 
     * @return the post-build strategy
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_NONE
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_SUCCESS
     */
	public int getPostbuildStrategy() {
		return postbuildStrategy;
	}

    /**
     * Sets the post-build strategy of this schedule.
     * 
     * @param postbuildStrategy the post-build strategy
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_NONE
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_SUCCESS
     */
	public void setPostbuildStrategy(int postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

    /**
     * Gets the build necessary condition for this schedule.
     * 
     * @return the build necessary condition
     */
	public String getBuildNecessaryCondition() {
		return buildNecessaryCondition;
	}

    /**
     * Sets the build necessary condition of this schedule.
     * 
     * @param buildNecessaryCondition the build necessary condition
     */
	public void setBuildNecessaryCondition(String buildNecessaryCondition) {
		this.buildNecessaryCondition = buildNecessaryCondition;
	}

    /**
     * Gets the dependent triggering strategy.
     * 
     * @return the dependent triggering strategy
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_ALL_DEPENDENT_SCHEDULES
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_NONE_DEPENDENT_SCHEDULES
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_DEPENDS_ON_THIS
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_THIS_DEPENDS_ON
     */
	public int getTriggerDependencyStrategy() {
		return triggerDependencyStrategy;
	}

    /**
     * Sets the dependent triggering strategy.
     * 
     * @param triggerDependencyStrategy the dependent triggering strategy
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_ALL_DEPENDENT_SCHEDULES
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_NONE_DEPENDENT_SCHEDULES
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_DEPENDS_ON_THIS
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_THIS_DEPENDS_ON
     */
	public void setTriggerDependencyStrategy(int triggerDependencyStrategy) {
		this.triggerDependencyStrategy = triggerDependencyStrategy;
	}

    /**
     * Gets the build cleanup strategy.
     * 
     * @return the build cleanup strategy.
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_CLEANUP_NONE
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_KEEP_BY_COUNT
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_KEEP_BY_DAYS
     */
	public int getBuildCleanupStrategy() {
		return buildCleanupStrategy;
	}

    /**
     * Sets the build cleanup strategy.
     * 
     * @param buildCleanupStrategy the build cleanup strategy.
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_CLEANUP_NONE
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_KEEP_BY_COUNT
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_KEEP_BY_DAYS
     */
	public void setBuildCleanupStrategy(int buildCleanupStrategy) {
		this.buildCleanupStrategy = buildCleanupStrategy;
	}

    /**
     * Gets the build cleanup data.
     * 
     * @return the build cleanup data
     */
	public String getBuildCleanupStrategyData() {
		return buildCleanupStrategyData;
	}

    /**
     * Sets the build cleanup data.
     * 
     * @param buildCleanupStrategyData the build cleanup data
     */
	public void setBuildCleanupStrategyData(String buildCleanupStrategyData) {
		this.buildCleanupStrategyData = buildCleanupStrategyData;
	}

    /**
     * Gets the next build version of this schedule.
     * 
     * @return the next build version
     */
	public String getNextVersion() {
		return nextVersion;
	}

    /**
     * Sets the build version number for the next build. This property is updated automatically
     * by the building process and can also be adjusted manually from user interface.
     * 
     * @param nextVersion the next build version
     */
	public void setNextVersion(String nextVersion) {
		this.nextVersion = nextVersion;
	}

    /**
     * Gets all variables, encoded as a string.
     * 
     * @return all variables encoded as a string
     */
    public String getVariables() {
        return this.variables;
    }

    /**
     * Sets all variables, encoded as a string.
     * 
     * @param variables all variables encoded as a string
     */
    public void setVariables(String variables) {
        this.variables = variables;
    }

    /**
     * Gets the list of associated builder names.
     * 
     * @return the list of associated builder names
     */
	public List getAssociatedBuilderNames() {
		return associatedBuilderNames;
	}

    /**
     * Sets the list of associated builder names for this schedule.
     * 
     * @param associatedBuilderNames the list of associated builder names
     */
	public void setAssociatedBuilderNames(List associatedBuilderNames) {
		this.associatedBuilderNames = associatedBuilderNames;
	}

    /**
     * Gets the list of associated post-builder names.
     * 
     * @return the list of associated post-builder names
     */
	public List getAssociatedPostbuilderNames() {
		return associatedPostbuilderNames;
	}

    /**
     * Sets the list of associated post-builder names for this schedule.
     * 
     * @param associatedPostbuilderNames the list of associated post-builder names
     */
	public void setAssociatedPostbuilderNames(List associatedPostbuilderNames) {
		this.associatedPostbuilderNames = associatedPostbuilderNames;
	}

    /**
     * Gets the identifier of the project of this schedule.
     * 
     * @return the identifier of the project
     */
	public long getProjectId() {
		return projectId;
	}

    /**
     * Sets the identifier of the project of this schedule.
     * 
     * @param projectId the identifier of the project
     */
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

    /**
     * Gets the list of schedule ids this schedule depends on.
     * 
     * @return the list of schedule ids
     */
	public List getDependentScheduleIds() {
		return dependentScheduleIds;
	}

    /**
     * Sets the list of schedule ids this schedule depends on.
     * 
     * @param dependentScheduleIds the list of schedule ids
     */
	public void setDependentScheduleIds(List dependentScheduleIds) {
		this.dependentScheduleIds = dependentScheduleIds;
	}

    /**
     * Gets the working path setting of this schedule.
     * 
     * @return the working path
     */
	public String getWorkingPath() {
		return workingPath;
	}

    /**
     * Sets the working path of this schedule.
     * 
     * @param workingPath the working path
     */
	public void setWorkingPath(String workingPath) {
		this.workingPath = workingPath;
	}
}
