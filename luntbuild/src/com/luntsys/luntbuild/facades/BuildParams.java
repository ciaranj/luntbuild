/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-3-10
 * Time: 9:25:57
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

package com.luntsys.luntbuild.facades;

/**
 * Parameters that control a build.
 * @author alvin shen
 */
public class BuildParams {
	private long scheduleId;
	private int buildType = -1;
	private String buildVersion = null;
	private int labelStrategy = -1;
	private int notifyStrategy = -1;
	private int postbuildStrategy = -1;
	private String buildNecessaryCondition = null;
	private int triggerDependencyStrategy = -1;

	/**
	 * Gets the the build type.
	 * 
	 * @return the build type
     * @see Constants#BUILD_TYPE_CLEAN
     * @see Constants#BUILD_TYPE_INCREMENT
	 */
	public int getBuildType() {
		return buildType;
	}

	/**
	 * Sets the the build type.
	 * 
	 * @param buildType the build type
     * @see Constants#BUILD_TYPE_CLEAN
     * @see Constants#BUILD_TYPE_INCREMENT
	 */
	public void setBuildType(int buildType) {
		this.buildType = buildType;
	}

	/**
	 * Gets the build version.
	 * 
	 * @return the build version
	 */
	public String getBuildVersion() {
		return buildVersion;
	}

	/**
	 * Sets the build version.
	 * 
	 * @param buildVersion the build version
	 */
	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	/**
	 * Gets the label strategy.
	 * 
	 * @return the label strategy
     * @see Constants#LABEL_ALWAYS
     * @see Constants#LABEL_NONE
     * @see Constants#LABEL_IF_SUCCESS
	 */
	public int getLabelStrategy() {
		return labelStrategy;
	}

	/**
	 * Sets the label strategy.
	 * 
	 * @param labelStrategy the label strategy
     * @see Constants#LABEL_ALWAYS
     * @see Constants#LABEL_NONE
     * @see Constants#LABEL_IF_SUCCESS
	 */
	public void setLabelStrategy(int labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

	/**
	 * Gets the notify strategy.
	 * 
	 * @return the notify strategy
     * @see Constants#NOTIFY_ALWAYS
     * @see Constants#NOTIFY_NONE
     * @see Constants#NOTIFY_IF_FAILED
     * @see Constants#NOTIFY_IF_SUCCESS
     * @see Constants#NOTIFY_WHEN_STATUS_CHANGED
     * @see Constants#NOTIFY_IF_FAILED_OR_CHANGED
	 */
	public int getNotifyStrategy() {
		return notifyStrategy;
	}

	/**
	 * Sets the notify strategy.
	 * 
	 * @param notifyStrategy the notify strategy
     * @see Constants#NOTIFY_ALWAYS
     * @see Constants#NOTIFY_NONE
     * @see Constants#NOTIFY_IF_FAILED
     * @see Constants#NOTIFY_IF_SUCCESS
     * @see Constants#NOTIFY_WHEN_STATUS_CHANGED
     * @see Constants#NOTIFY_IF_FAILED_OR_CHANGED
	 */
	public void setNotifyStrategy(int notifyStrategy) {
		this.notifyStrategy = notifyStrategy;
	}

	/**
	 * Gets the post-build strategy.
	 * 
	 * @return the post-build strategy
     * @see Constants#POSTBUILD_ALWAYS
     * @see Constants#POSTBUILD_NONE
     * @see Constants#POSTBUILD_IF_FAILED
     * @see Constants#POSTBUILD_IF_SUCCESS
	 */
	public int getPostbuildStrategy() {
		return postbuildStrategy;
	}

	/**
	 * Sets the post-build strategy.
	 * 
	 * @param postbuildStrategy the post-build strategy
     * @see Constants#POSTBUILD_ALWAYS
     * @see Constants#POSTBUILD_NONE
     * @see Constants#POSTBUILD_IF_FAILED
     * @see Constants#POSTBUILD_IF_SUCCESS
	 */
	public void setPostbuildStrategy(int postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

	/**
	 * Gets the build necessary condition.
	 * 
	 * @return the build necessary condition
	 */
	public String getBuildNecessaryCondition() {
		return buildNecessaryCondition;
	}

	/**
	 * Sets the build necessary condition.
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
     * @see Constants#TRIGGER_ALL_DEPENDENT_SCHEDULES
     * @see Constants#TRIGGER_NONE_DEPENDENT_SCHEDULES
     * @see Constants#TRIGGER_SCHEDULES_DEPENDS_ON_THIS
     * @see Constants#TRIGGER_SCHEDULES_THIS_DEPENDS_ON
	 */
	public int getTriggerDependencyStrategy() {
		return triggerDependencyStrategy;
	}

	/**
	 * Sets the dependent triggering strategy.
	 * 
	 * @param triggerDependencyStrategy the dependent triggering strategy
     * @see Constants#TRIGGER_ALL_DEPENDENT_SCHEDULES
     * @see Constants#TRIGGER_NONE_DEPENDENT_SCHEDULES
     * @see Constants#TRIGGER_SCHEDULES_DEPENDS_ON_THIS
     * @see Constants#TRIGGER_SCHEDULES_THIS_DEPENDS_ON
	 */
	public void setTriggerDependencyStrategy(int triggerDependencyStrategy) {
		this.triggerDependencyStrategy = triggerDependencyStrategy;
	}

	/**
	 * Gets the schedule identifier.
	 * 
	 * @return the schedule identifier
	 */
	public long getScheduleId() {
		return scheduleId;
	}

	/**
	 * Sets the schedule identifier.
	 * 
	 * @param scheduleId the schedule identifier
	 */
	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}
}
